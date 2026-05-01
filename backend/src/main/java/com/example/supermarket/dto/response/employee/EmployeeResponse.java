package com.example.supermarket.dto.response.employee;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {
    private Long id;
    private String username;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("hired_at")
    private LocalDateTime hiredAt;

    @JsonProperty("employee_type")
    private String employeeType; // "SALES", "WAREHOUSE", or "MANAGER"

    @JsonProperty("store_id")
    private Long storeId; // for sales employees

    @JsonProperty("warehouse_id")
    private Long warehouseId;
}
