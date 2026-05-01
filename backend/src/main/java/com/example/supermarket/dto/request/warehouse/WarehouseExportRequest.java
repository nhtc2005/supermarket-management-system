package com.example.supermarket.dto.request.warehouse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseExportRequest {
    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    @NotNull(message = "Reason is required")
    private String reason;

    @NotNull(message = "Employee ID is required")
    private Long employeeExportId;

    @NotEmpty(message = "Export items cannot be empty")
    @Valid
    private List<ExportItemRequest> items;
}
