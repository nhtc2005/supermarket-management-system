package com.example.supermarket.controller.warehouse;

import com.example.supermarket.dto.ApiResponse;
import com.example.supermarket.dto.request.store.StoreTransferRequest;
import com.example.supermarket.dto.request.warehouse.WarehouseExportRequest;
import com.example.supermarket.dto.request.warehouse.WarehouseImportRequest;
import com.example.supermarket.dto.response.store.StoreTransferResponse;
import com.example.supermarket.dto.response.warehouse.WarehouseExportResponse;
import com.example.supermarket.dto.response.warehouse.WarehouseImportResponse;
import com.example.supermarket.service.WarehouseOperationsServiceI;
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
@RequestMapping("/warehouse-operations")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Warehouse Operations", description = "Import, Export and Transfer operations")
public class WarehouseOperationsController {

    private final WarehouseOperationsServiceI warehouseOperationsService;

    // ==================== IMPORT OPERATIONS ====================

    /**
     * Import products into warehouse
     */
    @PostMapping("/imports")
    @PreAuthorize("hasAnyRole('WAREHOUSE', 'MANAGER')")
    @Operation(
            summary = "Import products into warehouse",
            description = "Create import record and add batches to warehouse inventory"
    )
    public ResponseEntity<ApiResponse<WarehouseImportResponse>> importProducts(
            @Valid @RequestBody WarehouseImportRequest request
    ) {
        WarehouseImportResponse response = warehouseOperationsService.importProducts(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Products imported successfully", response)
        );
    }

    /**
     * Get import history
     */
    @GetMapping("/imports")
    @PreAuthorize("hasAnyRole('WAREHOUSE', 'MANAGER')")
    @Operation(
            summary = "Get import history",
            description = "View all import records or filter by warehouse"
    )
    public ResponseEntity<ApiResponse<List<WarehouseImportResponse>>> getImportHistory(
            @RequestParam(required = false) Long warehouseId
    ) {
        List<WarehouseImportResponse> response = warehouseOperationsService.getImportHistory(warehouseId);

        return ResponseEntity.ok(
                ApiResponse.success("Import history retrieved successfully", response)
        );
    }

    // ==================== EXPORT OPERATIONS ====================

    /**
     * Export products from warehouse
     */
    @PostMapping("/exports")
    @PreAuthorize("hasAnyRole('WAREHOUSE', 'MANAGER')")
    @Operation(
            summary = "Export products from warehouse",
            description = "Create export record and reduce warehouse inventory"
    )
    public ResponseEntity<ApiResponse<WarehouseExportResponse>> exportProducts(
            @Valid @RequestBody WarehouseExportRequest request
    ) {
        WarehouseExportResponse response = warehouseOperationsService.exportProducts(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Products exported successfully", response)
        );
    }

    /**
     * Get export history
     */
    @GetMapping("/exports")
    @PreAuthorize("hasAnyRole('WAREHOUSE', 'MANAGER')")
    @Operation(
            summary = "Get export history",
            description = "View all export records or filter by warehouse"
    )
    public ResponseEntity<ApiResponse<List<WarehouseExportResponse>>> getExportHistory(
            @RequestParam(required = false) Long warehouseId
    ) {
        List<WarehouseExportResponse> response = warehouseOperationsService.getExportHistory(warehouseId);

        return ResponseEntity.ok(
                ApiResponse.success("Export history retrieved successfully", response)
        );
    }

    // ==================== TRANSFER OPERATIONS ====================

    /**
     * Transfer products from warehouse to store
     */
    @PostMapping("/transfers/to-store")
    @PreAuthorize("hasAnyRole('WAREHOUSE', 'MANAGER')")
    @Operation(
            summary = "Transfer products to store",
            description = "Move products from warehouse to store inventory"
    )
    public ResponseEntity<ApiResponse<StoreTransferResponse>> transferToStore(
            @Valid @RequestBody StoreTransferRequest request
    ) {
        StoreTransferResponse response = warehouseOperationsService.transferToStore(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Products transferred to store successfully", response)
        );
    }
}
