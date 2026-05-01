package com.example.supermarket.service;

import com.example.supermarket.dto.request.product.ProductVariantRequest;
import com.example.supermarket.dto.response.product.ProductVariantDetailResponse;

import java.util.List;

public interface ProductVariantServiceI {
    ProductVariantDetailResponse createVariant(ProductVariantRequest request);
    List<ProductVariantDetailResponse> getVariantsByProductId(Long productId);
    ProductVariantDetailResponse getVariantById(Long id);
    ProductVariantDetailResponse updateVariant(Long id, ProductVariantRequest request);
    void deleteVariant(Long id);
}
