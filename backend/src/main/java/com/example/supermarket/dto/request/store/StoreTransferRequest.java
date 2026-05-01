package com.example.supermarket.dto.request.store;

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
public class StoreTransferRequest {
    @NotNull(message = "From warehouse ID is required")
    private Long fromWarehouseId;

    @NotNull(message = "To store ID is required")
    private Long toStoreId;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    private String notes;

    @NotEmpty(message = "Transfer items cannot be empty")
    @Valid
    private List<TransferItemRequest> items;
}
