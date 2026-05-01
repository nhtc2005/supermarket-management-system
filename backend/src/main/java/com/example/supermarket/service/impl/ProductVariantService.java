package com.example.supermarket.service.impl;

import com.example.supermarket.dto.request.product.ProductVariantRequest;
import com.example.supermarket.dto.response.product.ProductVariantDetailResponse;
import com.example.supermarket.entity.Product;
import com.example.supermarket.entity.ProductVariant;
import com.example.supermarket.exception.NotFoundError;
import com.example.supermarket.repository.ProductRepository;
import com.example.supermarket.repository.ProductVariantRepository;
import com.example.supermarket.service.ProductVariantServiceI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductVariantService implements ProductVariantServiceI {
    private final ProductVariantRepository variantRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    @Transactional
    public ProductVariantDetailResponse createVariant(ProductVariantRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundError("Product not found"));

        String json;
        try {
            json = mapper.writeValueAsString(request.getVariantAttributes());
        } catch (Exception e) {
            throw new RuntimeException("Invalid variant attributes JSON");
        }

        ProductVariant variant = ProductVariant.builder()
                .productId(product.getId())
                .variantJson(json)
                .build();

        variant = variantRepository.save(variant);
        return toResponse(variant);
    }

    @Transactional(readOnly = true)
    public List<ProductVariantDetailResponse> getVariantsByProductId(Long productId) {
        return variantRepository.findByProductId(productId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductVariantDetailResponse getVariantById(Long id) {
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Variant not found"));
        return toResponse(variant);
    }

    @Transactional
    public ProductVariantDetailResponse updateVariant(Long id, ProductVariantRequest request) {
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Variant not found"));

        String json;
        try {
            json = mapper.writeValueAsString(request.getVariantAttributes());
        } catch (Exception e) {
            throw new RuntimeException("Invalid variant attributes JSON");
        }

        variant.setVariantJson(json);
        variant = variantRepository.save(variant);
        return toResponse(variant);
    }

    @Transactional
    public void deleteVariant(Long id) {
        if (!variantRepository.existsById(id)) {
            throw new NotFoundError("Variant not found");
        }
        variantRepository.deleteById(id);
    }

    private ProductVariantDetailResponse toResponse(ProductVariant variant) {
        Product product = productRepository.findById(variant.getProductId())
                .orElseThrow(() -> new NotFoundError("Product not found"));

        Map<String, Object> attributes;

        try {
            attributes = mapper.readValue(
                    variant.getVariantJson(),
                    new TypeReference<Map<String, Object>>() {}
            );
        } catch (Exception e) {
            log.error("Failed to parse variant JSON", e);
            attributes = new HashMap<>(); // fallback
        }

        return ProductVariantDetailResponse.builder()
                .id(variant.getId())
                .productId(product.getId())
                .productName(product.getName())
                .variantAttributes(attributes)
                .build();
    }
}
