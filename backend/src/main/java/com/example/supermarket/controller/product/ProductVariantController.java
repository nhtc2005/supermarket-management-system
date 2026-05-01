package com.example.supermarket.controller.product;

import com.example.supermarket.dto.ApiResponse;
import com.example.supermarket.dto.request.product.ProductVariantRequest;
import com.example.supermarket.dto.response.product.ProductVariantDetailResponse;
import com.example.supermarket.service.ProductVariantServiceI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products/{productId}/variants")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Product Variant", description = "Product variant management APIs")
public class ProductVariantController {
    private final ProductVariantServiceI variantService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create product variant")
    public ResponseEntity<ApiResponse<ProductVariantDetailResponse>> createVariant(
            @PathVariable Long productId,
            @Valid @RequestBody ProductVariantRequest request
    ) {
        request.setProductId(productId);
        ProductVariantDetailResponse response = variantService.createVariant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Variant created successfully", response)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SALES', 'WAREHOUSE', 'MANAGER')")
    @Operation(summary = "Get all variants of a product")
    public ResponseEntity<ApiResponse<List<ProductVariantDetailResponse>>> getVariantsByProduct(
            @PathVariable Long productId
    ) {
        List<ProductVariantDetailResponse> response = variantService.getVariantsByProductId(productId);
        return ResponseEntity.ok(
                ApiResponse.success("Variants retrieved successfully", response)
        );
    }

    @GetMapping("/{variantId}")
    @PreAuthorize("hasAnyRole('SALES', 'WAREHOUSE', 'MANAGER')")
    @Operation(summary = "Get variant by ID")
    public ResponseEntity<ApiResponse<ProductVariantDetailResponse>> getVariantById(
            @PathVariable Long variantId
    ) {
        ProductVariantDetailResponse response = variantService.getVariantById(variantId);
        return ResponseEntity.ok(
                ApiResponse.success("Variant retrieved successfully", response)
        );
    }

    @PutMapping("/{variantId}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update variant")
    public ResponseEntity<ApiResponse<ProductVariantDetailResponse>> updateVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @Valid @RequestBody ProductVariantRequest request
    ) {
        request.setProductId(productId);
        ProductVariantDetailResponse response = variantService.updateVariant(variantId, request);
        return ResponseEntity.ok(
                ApiResponse.success("Variant updated successfully", response)
        );
    }

    @DeleteMapping("/{variantId}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete variant")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(@PathVariable Long variantId) {
        variantService.deleteVariant(variantId);
        return ResponseEntity.ok(
                ApiResponse.success("Variant deleted successfully", null)
        );
    }
}
