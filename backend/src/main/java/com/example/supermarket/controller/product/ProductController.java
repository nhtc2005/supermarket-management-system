package com.example.supermarket.controller.product;

import com.example.supermarket.dto.ApiResponse;
import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.product.ProductCreateRequest;
import com.example.supermarket.dto.request.product.ProductSearchRequest;
import com.example.supermarket.dto.request.product.ProductUpdateRequest;
import com.example.supermarket.dto.response.product.ProductDetailResponse;
import com.example.supermarket.dto.response.product.ProductResponse;
import com.example.supermarket.dto.response.product.StoreStockResponse;
import com.example.supermarket.service.ProductServiceI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Product", description = "Product management APIs")
public class ProductController {
    private final ProductServiceI productService;

    /**
     * Create new product
     */
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create product", description = "Manager can create new product")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductCreateRequest request
    ) {
        ProductResponse response = productService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Product created successfully", response)
        );
    }

    /**
     * Search products with filters
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SALES', 'WAREHOUSE', 'MANAGER')")
    @Operation(summary = "Search products", description = "All users can search products")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) String barcode,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        ProductSearchRequest searchRequest = new ProductSearchRequest(
                name, sku, barcode, minPrice, maxPrice, page, size, sortBy, sortDirection
        );

        PageResponse<ProductResponse> response = productService.searchProducts(searchRequest);

        return ResponseEntity.ok(
                ApiResponse.success("Products retrieved successfully", response)
        );
    }

    /**
     * Get product by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SALES', 'WAREHOUSE', 'MANAGER')")
    @Operation(summary = "Get product by ID", description = "All users can view product details")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductById(
            @PathVariable Long id
    ) {
        ProductDetailResponse response = productService.getProductById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Product retrieved successfully", response)
        );
    }

    /**
     * Update product
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update product", description = "Manager can update product information")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request
    ) {
        ProductResponse response = productService.updateProduct(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Product updated successfully", response)
        );
    }

    /**
     * Delete product
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete product", description = "Manager can delete product")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id
    ) {
        productService.deleteProduct(id);

        return ResponseEntity.ok(
                ApiResponse.success("Product deleted successfully", null)
        );
    }

    /**
     * Get product stock in all stores
     */
    @GetMapping("/{id}/stores")
    @PreAuthorize("hasAnyRole('SALES', 'WAREHOUSE', 'MANAGER')")
    @Operation(summary = "Get product stock in stores", description = "View product stock across all stores")
    public ResponseEntity<ApiResponse<List<StoreStockResponse>>> getProductStockInStores(
            @PathVariable Long id
    ) {
        List<StoreStockResponse> response = productService.getProductStockInStores(id);

        return ResponseEntity.ok(
                ApiResponse.success("Product stock retrieved successfully", response)
        );
    }

    /**
     * Get low stock products
     */
    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('WAREHOUSE', 'MANAGER')")
    @Operation(summary = "Get low stock products", description = "View products with stock below threshold")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        PageResponse<ProductResponse> response = productService.getLowStockProducts(threshold, page, size);

        return ResponseEntity.ok(
                ApiResponse.success("Low stock products retrieved successfully", response)
        );
    }
}
