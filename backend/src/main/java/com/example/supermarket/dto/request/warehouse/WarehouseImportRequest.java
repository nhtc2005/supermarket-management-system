package com.example.supermarket.dto.request.warehouse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseImportRequest {

    @NotNull(message = "Warehouse Id is required")
    private Long warehouseId;

    @NotNull(message = "Supplier is required")
    private String supplier;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", message = "Unit price must be positive")
    private BigDecimal unitPrice;

    @NotNull(message = "Employee ID is required")
    private Long employeeImportId;

    @NotEmpty(message = "Import items cannot be empty")
    @Valid
    private List<ImportItemRequest> items;
}
