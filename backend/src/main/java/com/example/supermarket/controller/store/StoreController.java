package com.example.supermarket.controller.store;

import com.example.supermarket.dto.ApiResponse;
import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.store.StoreRequest;
import com.example.supermarket.dto.response.store.StoreResponse;
import com.example.supermarket.service.StoreServiceI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Store", description = "Store management APIs")
public class StoreController {
    private final StoreServiceI storeService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create store")
    public ResponseEntity<ApiResponse<StoreResponse>> createStore(
            @Valid @RequestBody StoreRequest request
    ) {
        StoreResponse response = storeService.createStore(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Store created successfully", response)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SALES', 'MANAGER', 'WAREHOUSE')")
    @Operation(summary = "Get all stores")
    public ResponseEntity<ApiResponse<PageResponse<StoreResponse>>> getAllStores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<StoreResponse> response = storeService.getAllStores(page, size);
        return ResponseEntity.ok(
                ApiResponse.success("Stores retrieved successfully", response)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SALES', 'MANAGER')")
    @Operation(summary = "Get store by ID")
    public ResponseEntity<ApiResponse<StoreResponse>> getStoreById(@PathVariable Long id) {
        StoreResponse response = storeService.getStoreById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Store retrieved successfully", response)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update store")
    public ResponseEntity<ApiResponse<StoreResponse>> updateStore(
            @PathVariable Long id,
            @Valid @RequestBody StoreRequest request
    ) {
        StoreResponse response = storeService.updateStore(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Store updated successfully", response)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete store")
    public ResponseEntity<ApiResponse<Void>> deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
        return ResponseEntity.ok(
                ApiResponse.success("Store deleted successfully", null)
        );
    }
}
