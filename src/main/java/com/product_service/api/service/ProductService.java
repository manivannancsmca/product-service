package com.product_service.api.service;

import java.util.List;

import com.product_service.api.dto.ProductDTO;
import com.product_service.api.dto.StockUpdateRequest;

public interface ProductService {
    
    public ProductDTO reserveStock(StockUpdateRequest request);

    public void restoreStock(StockUpdateRequest request);

    public ProductDTO getProductById(Long id);

    public List<ProductDTO> getAllProducts();

    public ProductDTO createProduct(ProductDTO dto);
}