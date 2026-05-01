package com.example.supermarket.controller.warehouse;

import com.example.supermarket.dto.ApiResponse;
import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.warehouse.WarehouseRequest;
import com.example.supermarket.dto.response.warehouse.WarehouseResponse;
import com.example.supermarket.service.WarehouseServiceI;
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
@RequestMapping("/warehouses")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Warehouse", description = "Warehouse management APIs")
public class WarehouseController {
    private final WarehouseServiceI warehouseService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create warehouse")
    public ResponseEntity<ApiResponse<WarehouseResponse>> createWarehouse(
            @Valid @RequestBody WarehouseRequest request
    ) {
        WarehouseResponse response = warehouseService.createWarehouse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Warehouse created successfully", response)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('WAREHOUSE', 'MANAGER')")
    @Operation(summary = "Get all warehouses")
    public ResponseEntity<ApiResponse<PageResponse<WarehouseResponse>>> getAllWarehouses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<WarehouseResponse> response = warehouseService.getAllWarehouses(page, size);
        return ResponseEntity.ok(
                ApiResponse.success("Warehouses retrieved successfully", response)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('WAREHOUSE', 'MANAGER')")
    @Operation(summary = "Get warehouse by ID")
    public ResponseEntity<ApiResponse<WarehouseResponse>> getWarehouseById(@PathVariable Long id) {
        WarehouseResponse response = warehouseService.getWarehouseById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Warehouse retrieved successfully", response)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update warehouse")
    public ResponseEntity<ApiResponse<WarehouseResponse>> updateWarehouse(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseRequest request
    ) {
        WarehouseResponse response = warehouseService.updateWarehouse(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Warehouse updated successfully", response)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete warehouse")
    public ResponseEntity<ApiResponse<Void>> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.ok(
                ApiResponse.success("Warehouse deleted successfully", null)
        );
    }
}
