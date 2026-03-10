package com.product_service.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.product_service.api.dto.ApiResponse;
import com.product_service.api.dto.ProductDTO;
import com.product_service.api.dto.StockUpdateRequest;
import com.product_service.api.service.ProductService;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts() {
        return ResponseEntity.ok(
            ApiResponse.success("Products fetched", productService.getAllProducts()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponse.success("Product fetched", productService.getProductById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@RequestBody ProductDTO dto) {
        return ResponseEntity.ok(
            ApiResponse.success("Product created", productService.createProduct(dto)));
    }

    // ─── SAGA Endpoint: Called by Order Service to reserve stock ───────────
    @PostMapping("/reserve-stock")
    public ResponseEntity<ApiResponse<ProductDTO>> reserveStock(
            @RequestBody StockUpdateRequest request) {
        log.info("[SAGA] Reserve stock request received: {}", request);
        ProductDTO dto = productService.reserveStock(request);
        return ResponseEntity.ok(ApiResponse.success("Stock reserved successfully", dto));
    }

    // ─── SAGA Compensation Endpoint: Called to rollback stock ──────────────
    @PostMapping("/restore-stock")
    public ResponseEntity<ApiResponse<Void>> restoreStock(
            @RequestBody StockUpdateRequest request) {
        log.warn("[SAGA COMPENSATE] Restore stock request received: {}", request);
        productService.restoreStock(request);
        return ResponseEntity.ok(ApiResponse.success("Stock restored (compensated)", null));
    }
}
