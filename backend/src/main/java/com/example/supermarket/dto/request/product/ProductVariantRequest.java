package com.example.supermarket.dto.request.product;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;

    private Map<String, Object> variantAttributes;
}
