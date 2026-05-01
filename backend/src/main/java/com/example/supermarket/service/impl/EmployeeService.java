package com.example.supermarket.service.impl;

import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.employee.EmployeeRequest;
import com.example.supermarket.dto.request.employee.SalesEmployeeRequest;
import com.example.supermarket.dto.request.employee.WarehouseEmployeeRequest;
import com.example.supermarket.dto.response.employee.EmployeeResponse;
import com.example.supermarket.entity.*;
import com.example.supermarket.exception.ConflictError;
import com.example.supermarket.exception.NotFoundError;
import com.example.supermarket.mapper.EmployeeMapper;
import com.example.supermarket.repository.*;
import com.example.supermarket.service.EmployeeServiceI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService implements EmployeeServiceI {
    private final EmployeeRepository employeeRepository;
    private final SalesEmployeeRepository salesEmployeeRepository;
    private final WarehouseEmployeeRepository warehouseEmployeeRepository;
    private final StoreRepository storeRepository;
    private final WarehouseRepository warehouseRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByUsername(request.getUsername())) {
            throw new ConflictError("Username already exists");
        }

        Employee employee = Employee.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .hiredAt(LocalDateTime.now())
                .build();

        employee = employeeRepository.save(employee);
        return employeeMapper.toResponse(employee, "MANAGER");
    }

    @Transactional
    public EmployeeResponse createSalesEmployee(SalesEmployeeRequest request) {
        Employee employee = createEmployeeBase(request.getEmployee());

        Store store = request.getStoreId() != null
                ? storeRepository.findById(request.getStoreId()).orElse(null)
                : null;

        Long storeId = store != null ? store.getId() :  null;
        SalesEmployee salesEmployee = SalesEmployee.builder()
                .employeeID(employee.getId())
                .storeID(storeId)
                .totalSales(0.0)
                .build();

        salesEmployeeRepository.save(salesEmployee);
        return employeeMapper.toResponse(employee, "SALES");
    }

    @Transactional
    public EmployeeResponse createWarehouseEmployee(WarehouseEmployeeRequest request) {
        Employee employee = createEmployeeBase(request.getEmployee());

        Warehouse warehouse = request.getWarehouseId() != null
                ? warehouseRepository.findById(request.getWarehouseId()).orElse(null)
                : null;

        Long warehouseId = warehouse != null ? warehouse.getId() : null;
        WarehouseEmployee warehouseEmployee = WarehouseEmployee.builder()
                .employeeID(employee.getId())
                .warehouseID(warehouseId)
                .build();

        warehouseEmployeeRepository.save(warehouseEmployee);
        return employeeMapper.toResponse(employee, "WAREHOUSE");
    }

    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> getAllEmployees(int page, int size) {
        Page<Employee> employeePage = employeeRepository.findAll(PageRequest.of(page, size));

        return PageResponse.<EmployeeResponse>builder()
                .content(employeePage.getContent().stream()
                        .map(emp -> employeeMapper.toResponse(emp, determineType(emp.getId())))
                        .toList())
                .pageNumber(employeePage.getNumber())
                .pageSize(employeePage.getSize())
                .totalElements(employeePage.getTotalElements())
                .totalPages(employeePage.getTotalPages())
                .isLast(employeePage.isLast())
                .build();
    }

    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new NotFoundError("Employee not found");
        }
        employeeRepository.deleteById(id);
    }

    private Employee createEmployeeBase(EmployeeRequest request) {
        if (employeeRepository.existsByUsername(request.getUsername())) {
            throw new ConflictError("Username already exists");
        }

        Employee employee = Employee.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .hiredAt(LocalDateTime.now())
                .build();

        return employeeRepository.save(employee);
    }

    private String determineType(Long employeeId) {
        if (salesEmployeeRepository.existsById(employeeId)) return "SALES";
        if (warehouseEmployeeRepository.existsById(employeeId)) return "WAREHOUSE";
        return "MANAGER";
    }
}
