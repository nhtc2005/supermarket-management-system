package com.example.supermarket.service;

import com.example.supermarket.dto.request.auth.ChangePasswordRequest;
import com.example.supermarket.dto.request.auth.LoginRequest;
import com.example.supermarket.dto.request.auth.RefreshTokenRequest;
import com.example.supermarket.dto.request.customer.CustomerRegisterRequest;
import com.example.supermarket.dto.response.auth.AuthResponse;

public interface AuthServiceI {

    /**
     * Register a new customer
     */
    AuthResponse registerCustomer(CustomerRegisterRequest request);

    /**
     * Universal login for both customers and employees
     */
    AuthResponse login(LoginRequest request);

    /**
     * Refresh access token using a valid refresh token
     */
    AuthResponse refreshToken(RefreshTokenRequest refreshToken);

    /**
     * Get current user info from access token
     */
    Object getCurrentUser(String accessToken);

    /**
     * Change password for customer
     */
    void changeCustomerPassword(Long customerId, ChangePasswordRequest request);

    /**
     * Change password for employee
     */
    void changeEmployeePassword(Long employeeId, ChangePasswordRequest request);
}
