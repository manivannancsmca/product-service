package com.product_service.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.product_service.api.dto.ProductDTO;
import com.product_service.api.dto.StockUpdateRequest;
import com.product_service.api.entity.Product;
import com.product_service.api.exception.InsufficientStockException;
import com.product_service.api.exception.ProductNotFoundException;
import com.product_service.api.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    // ─── SAGA STEP 1: Reserve/Deduct stock ──────────────────────────────────
    @Transactional
    @Override
    public ProductDTO reserveStock(StockUpdateRequest request) {

        log.info("SAGA [RESERVE STOCK] → productId={}, qty={}",
                request.getProductId(), request.getQuantity());

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found: " + request.getProductId()));

        // Validate stock before attempting update
        if (product.getStock() < request.getQuantity()) {
            log.error("SAGA [RESERVE STOCK FAILED] → Insufficient stock. " +
                    "Available: {}, Requested: {}",
                    product.getStock(), request.getQuantity());
            throw new InsufficientStockException(
                    String.format("Insufficient stock! Available: %d, Requested: %d",
                            product.getStock(), request.getQuantity()));
        }

        int updated = productRepository.decrementStock(
                request.getProductId(), request.getQuantity());

        if (updated == 0) {
            throw new InsufficientStockException("Stock reservation failed due to race condition.");
        }

        product = productRepository.findById(request.getProductId()).get();

        log.info("SAGA [RESERVE STOCK SUCCESS] → productId={}, remainingStock={}",
                product.getId(), product.getStock());

        return mapToDTO(product);
    }

    @Transactional
    @Override
    public void restoreStock(StockUpdateRequest request) {
        log.warn("SAGA [COMPENSATE] → Restoring stock for productId={}, qty={}",
                request.getProductId(), request.getQuantity());

        productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found for compensation: " + request.getProductId()));

        productRepository.incrementStock(request.getProductId(), request.getQuantity());

        log.warn("SAGA [COMPENSATE SUCCESS] → Stock restored for productId={}",
                request.getProductId());
    }

    @Transactional(readOnly = true)
    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        return mapToDTO(product);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ProductDTO createProduct(ProductDTO dto) {
        Product product = Product.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .build();
        product = productRepository.save(product);
        log.info("Product created: {}", product.getId());
        return mapToDTO(product);
    }

    private ProductDTO mapToDTO(Product p) {
        return ProductDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .price(p.getPrice())
                .stock(p.getStock())
                .build();
    }

}
