package com.example.supermarket.mapper;

import com.example.supermarket.dto.response.employee.EmployeeResponse;
import com.example.supermarket.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    @Mapping(target = "employeeType", source = "employeeType")
    EmployeeResponse toResponse(Employee employee, String employeeType);
}
