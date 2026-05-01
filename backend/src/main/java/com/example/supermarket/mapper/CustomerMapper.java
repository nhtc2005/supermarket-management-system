package com.example.supermarket.mapper;

import com.example.supermarket.dto.response.customer.CustomerDetailResponse;
import com.example.supermarket.dto.response.customer.CustomerResponse;
import com.example.supermarket.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerResponse toResponse(Customer customer);
    @Mapping(target = "totalOrders", source = "totalOrders")
    @Mapping(target = "totalSpent", source = "totalSpent")
    CustomerDetailResponse toDetailResponse(
            Customer customer,
            Long totalOrders,
            Double totalSpent
    );



}
