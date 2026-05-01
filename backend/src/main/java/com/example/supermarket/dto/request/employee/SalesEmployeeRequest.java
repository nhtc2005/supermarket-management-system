package com.example.supermarket.dto.request.employee;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesEmployeeRequest {
    @Valid
    @NotNull(message = "Employee data is required")
    private EmployeeRequest employee;

    private Long storeId;
}
