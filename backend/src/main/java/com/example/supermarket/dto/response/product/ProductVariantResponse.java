package com.example.supermarket.dto.response.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantResponse {
    private Long id;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("variant_attributes")
    private Map<String, Object> variantAttributes;

    @JsonProperty("total_stock")
    private Integer totalStock;
}
