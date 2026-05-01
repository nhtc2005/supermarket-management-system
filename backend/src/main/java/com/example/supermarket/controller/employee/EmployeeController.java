package com.example.supermarket.controller.employee;

import com.example.supermarket.dto.ApiResponse;
import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.employee.EmployeeRequest;
import com.example.supermarket.dto.request.employee.SalesEmployeeRequest;
import com.example.supermarket.dto.request.employee.WarehouseEmployeeRequest;
import com.example.supermarket.dto.response.employee.EmployeeResponse;
import com.example.supermarket.service.EmployeeServiceI;
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
@RequestMapping("/employees")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Employee", description = "Employee management APIs")
public class EmployeeController {
    private final EmployeeServiceI employeeService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(
            @Valid @RequestBody EmployeeRequest request
    ) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Employee created successfully", response)
        );
    }

    @PostMapping("/sales")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create sales employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createSalesEmployee(
            @Valid @RequestBody SalesEmployeeRequest request
    ) {
        EmployeeResponse response = employeeService.createSalesEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Sales employee created successfully", response)
        );
    }

    @PostMapping("/warehouse")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create warehouse employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createWarehouseEmployee(
            @Valid @RequestBody WarehouseEmployeeRequest request
    ) {
        EmployeeResponse response = employeeService.createWarehouseEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Warehouse employee created successfully", response)
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Get all employees")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponse>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<EmployeeResponse> response = employeeService.getAllEmployees(page, size);
        return ResponseEntity.ok(
                ApiResponse.success("Employees retrieved successfully", response)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete employee")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(
                ApiResponse.success("Employee deleted successfully", null)
        );
    }
}
