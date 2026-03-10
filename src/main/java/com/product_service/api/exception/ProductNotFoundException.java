package com.product_service.api.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) { super(message); }
}
