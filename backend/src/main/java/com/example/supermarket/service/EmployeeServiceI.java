package com.example.supermarket.service;

import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.employee.EmployeeRequest;
import com.example.supermarket.dto.request.employee.SalesEmployeeRequest;
import com.example.supermarket.dto.request.employee.WarehouseEmployeeRequest;
import com.example.supermarket.dto.response.employee.EmployeeResponse;

public interface EmployeeServiceI {
    EmployeeResponse createEmployee(EmployeeRequest request);
    EmployeeResponse createSalesEmployee(SalesEmployeeRequest request);
    EmployeeResponse createWarehouseEmployee(WarehouseEmployeeRequest request);
    PageResponse<EmployeeResponse> getAllEmployees(int page, int size);
    void deleteEmployee(Long id);
}
