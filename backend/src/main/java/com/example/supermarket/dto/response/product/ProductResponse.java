package com.example.supermarket.dto.response.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;

    @JsonProperty("sku")
    private String sku;

    private String barcode;

    @JsonProperty("total_variants")
    private Integer totalVariants;

    @JsonProperty("available_quantity")
    private Integer availableQuantity;
}
