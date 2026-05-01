package com.example.supermarket.dto.response.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreStockResponse {
    @JsonProperty("store_id")
    private Long storeId;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("quantity_in_stock")
    private Integer quantityInStock;
}
