package com.example.supermarket.dto.response.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailResponse {
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

    private List<ProductVariantResponse> variants;

    @JsonProperty("store_stocks")
    private List<StoreStockResponse> storeStocks;
}
