package com.example.supermarket.service;

import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.customer.CustomerSearchRequest;
import com.example.supermarket.dto.request.customer.CustomerUpdateRequest;
import com.example.supermarket.dto.response.customer.CustomerDetailResponse;
import com.example.supermarket.dto.response.customer.CustomerResponse;

public interface CustomerServiceI {
    void deleteCustomer(Long customerId);
    CustomerResponse addLoyaltyPoints(Long customerId, Integer points);
    CustomerResponse redeemLoyaltyPoints(Long customerId, Integer points);
    PageResponse<CustomerDetailResponse> getTopCustomers(Integer page, Integer size);
    CustomerResponse updateCustomer(Long customerId, CustomerUpdateRequest request);
    PageResponse<CustomerResponse> searchCustomers(CustomerSearchRequest request);
    CustomerDetailResponse getMyProfile(Long customerId);
    CustomerDetailResponse getCustomerById(Long id);
}
