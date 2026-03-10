package com.product_service.api.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateRequest {
    private Long productId;
    private Integer quantity;
}
