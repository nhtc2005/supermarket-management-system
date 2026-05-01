package com.example.supermarket.controller.batch;

import com.example.supermarket.dto.ApiResponse;
import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.batch.BatchRequest;
import com.example.supermarket.dto.response.batch.BatchResponse;
import com.example.supermarket.service.BatchServiceI;
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
@RequestMapping("/batches")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Batch", description = "Batch management APIs")
public class BatchController {
    private final BatchServiceI batchService;

    @PostMapping
    @PreAuthorize("hasAnyRole('WAREHOUSE', 'MANAGER')")
    @Operation(summary = "Create batch")
    public ResponseEntity<ApiResponse<BatchResponse>> createBatch(
            @Valid @RequestBody BatchRequest request
    ) {
        BatchResponse response = batchService.createBatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Batch created successfully", response)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('WAREHOUSE', 'MANAGER')")
    @Operation(summary = "Get all batches")
    public ResponseEntity<ApiResponse<PageResponse<BatchResponse>>> getAllBatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<BatchResponse> response = batchService.getAllBatches(page, size);
        return ResponseEntity.ok(
                ApiResponse.success("Batches retrieved successfully", response)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('WAREHOUSE', 'MANAGER')")
    @Operation(summary = "Get batch by ID")
    public ResponseEntity<ApiResponse<BatchResponse>> getBatchById(@PathVariable Long id) {
        BatchResponse response = batchService.getBatchById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Batch retrieved successfully", response)
        );
    }

    @GetMapping("/expired")
    @PreAuthorize("hasAnyRole('WAREHOUSE', 'MANAGER')")
    @Operation(summary = "Get expired batches")
    public ResponseEntity<ApiResponse<PageResponse<BatchResponse>>> getExpiredBatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<BatchResponse> response = batchService.getExpiredBatches(page, size);
        return ResponseEntity.ok(
                ApiResponse.success("Expired batches retrieved successfully", response)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete batch")
    public ResponseEntity<ApiResponse<Void>> deleteBatch(@PathVariable Long id) {
        batchService.deleteBatch(id);
        return ResponseEntity.ok(
                ApiResponse.success("Batch deleted successfully", null)
        );
    }
}
