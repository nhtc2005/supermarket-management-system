package com.example.supermarket.service.impl;

import com.example.supermarket.dto.request.auth.ChangePasswordRequest;
import com.example.supermarket.dto.request.auth.LoginRequest;
import com.example.supermarket.dto.request.auth.RefreshTokenRequest;
import com.example.supermarket.dto.request.customer.CustomerRegisterRequest;
import com.example.supermarket.dto.response.auth.AuthResponse;
import com.example.supermarket.dto.response.customer.CustomerResponse;
import com.example.supermarket.dto.response.employee.EmployeeResponse;
import com.example.supermarket.entity.Customer;
import com.example.supermarket.entity.Employee;
import com.example.supermarket.exception.BadRequestError;
import com.example.supermarket.exception.ConflictError;
import com.example.supermarket.exception.UnauthorizedError;
import com.example.supermarket.mapper.CustomerMapper;
import com.example.supermarket.mapper.EmployeeMapper;
import com.example.supermarket.repository.CustomerRepository;
import com.example.supermarket.repository.EmployeeRepository;
import com.example.supermarket.repository.SalesEmployeeRepository;
import com.example.supermarket.repository.WarehouseEmployeeRepository;
import com.example.supermarket.service.AuthServiceI;
import com.example.supermarket.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements AuthServiceI {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final SalesEmployeeRepository salesEmployeeRepository;
    private final WarehouseEmployeeRepository warehouseEmployeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomerMapper customerMapper;
    private final EmployeeMapper employeeMapper;

    /**
     * Customer registration
     */
    @Transactional
    public AuthResponse registerCustomer(CustomerRegisterRequest request) {
        log.info("Registering new customer with email: {}", request.getEmail());

        // Check if email exists
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new ConflictError("Email already exists");
        }

        System.out.println(passwordEncoder.encode("manager123"));

        // Create new customer
        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .password(passwordEncoder.encode(request.getPassword()))
                .loyaltyPoints(0)
                .build();

        customer = customerRepository.save(customer);
        log.info("Customer registered successfully with ID: {}", customer.getId());

        // Generate tokens
        String accessToken = jwtUtil.generateCustomerAccessToken(
                customer.getId(),
                customer.getEmail()
        );
        String refreshToken = jwtUtil.generateRefreshToken(
                customer.getEmail(),
                "CUSTOMER",
                customer.getId()
        );

        CustomerResponse customerResponse = customerMapper.toResponse(customer);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userType("CUSTOMER")
                .user(customerResponse)
                .build();
    }

    /**
     * Universal login for both customers and employees
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt with identifier: {}", request.getIdentifier());

        if (request.getIdentifier().contains("@")) {
            return loginAsCustomer(request);
        }

        return loginAsEmployee(request);
    }

    /**
     * Login as customer
     */
    private AuthResponse loginAsCustomer(LoginRequest request) {
        Customer customer = customerRepository.findByEmail(request.getIdentifier())
                .orElseThrow(() -> new UnauthorizedError("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            throw new UnauthorizedError("Invalid email or password");
        }

        log.info("Customer logged in successfully: {}", customer.getEmail());

        // Generate tokens
        String accessToken = jwtUtil.generateCustomerAccessToken(
                customer.getId(),
                customer.getEmail()
        );
        String refreshToken = jwtUtil.generateRefreshToken(
                customer.getEmail(),
                "CUSTOMER",
                customer.getId()
        );

        CustomerResponse customerResponse = customerMapper.toResponse(customer);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userType("CUSTOMER")
                .user(customerResponse)
                .build();
    }

    /**
     * Login as employee
     */
    private AuthResponse loginAsEmployee(LoginRequest request) {
        Employee employee = employeeRepository.findByUsername(request.getIdentifier())
                .orElseThrow(() -> new UnauthorizedError("Invalid username or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
            throw new UnauthorizedError("Invalid username or password");
        }

        log.info("Employee logged in successfully: {}", employee.getUsername());

        // Determine employee type
        String employeeType = determineEmployeeType(employee.getId());

        // Generate tokens
        String accessToken = jwtUtil.generateEmployeeAccessToken(
                employee.getId(),
                employee.getUsername(),
                employeeType
        );
        String refreshToken = jwtUtil.generateRefreshToken(
                employee.getUsername(),
                "EMPLOYEE",
                employee.getId()
        );

        EmployeeResponse employeeResponse = employeeMapper.toResponse(employee, employeeType);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userType("EMPLOYEE")
                .user(employeeResponse)
                .build();
    }

    /**
     * Determine employee type based on related tables
     */
    private String determineEmployeeType(Long employeeId) {
        if (salesEmployeeRepository.existsById(employeeId)) {
            return "SALES";
        }
        if (warehouseEmployeeRepository.existsById(employeeId)) {
            return "WAREHOUSE";
        }
        return "MANAGER"; // Default for regular employees
    }

    /**
     * Refresh access token
     */
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refreshing access token");
        String refreshToken = request.getRefreshToken();
        try {
            // Extract claims from refresh token
            String subject = jwtUtil.extractSubject(refreshToken);
            String userType = jwtUtil.extractUserType(refreshToken);
            Long userId = jwtUtil.extractUserId(refreshToken);

            // Validate refresh token
            if (!jwtUtil.validateToken(refreshToken, subject)) {
                throw new UnauthorizedError("Invalid or expired refresh token");
            }

            // Generate new access token based on user type
            if ("CUSTOMER".equals(userType)) {
                Customer customer = customerRepository.findById(userId)
                        .orElseThrow(() -> new UnauthorizedError("Customer not found"));

                String newAccessToken = jwtUtil.generateCustomerAccessToken(
                        customer.getId(),
                        customer.getEmail()
                );

                return AuthResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .userType("CUSTOMER")
                        .user(customerMapper.toResponse(customer))
                        .build();
            } else {
                Employee employee = employeeRepository.findById(userId)
                        .orElseThrow(() -> new UnauthorizedError("Employee not found"));

                String employeeType = determineEmployeeType(employee.getId());
                String newAccessToken = jwtUtil.generateEmployeeAccessToken(
                        employee.getId(),
                        employee.getUsername(),
                        employeeType
                );

                return AuthResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .userType("EMPLOYEE")
                        .user(employeeMapper.toResponse(employee, employeeType))
                        .build();
            }
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw new UnauthorizedError("Invalid refresh token");
        }
    }

    /**
     * Get current user info from token
     */
    @Transactional(readOnly = true)
    public Object getCurrentUser(String token) {
        String userType = jwtUtil.extractUserType(token);
        Long userId = jwtUtil.extractUserId(token);

        if ("CUSTOMER".equals(userType)) {
            Customer customer = customerRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestError("Customer not found"));
            return customerMapper.toResponse(customer);
        } else {
            Employee employee = employeeRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestError("Employee not found"));
            String employeeType = determineEmployeeType(employee.getId());
            return employeeMapper.toResponse(employee, employeeType);
        }
    }

    /**
     * Change password for customer
     */
    @Transactional
    public void changeCustomerPassword(Long customerId, ChangePasswordRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BadRequestError("Customer not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), customer.getPassword())) {
            throw new BadRequestError("Current password is incorrect");
        }

        // Update password
        customer.setPassword(passwordEncoder.encode(request.getNewPassword()));
        customerRepository.save(customer);

        log.info("Password changed for customer ID: {}", customerId);
    }

    /**
     * Change password for employee
     */
    @Transactional
    public void changeEmployeePassword(Long employeeId, ChangePasswordRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BadRequestError("Employee not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), employee.getPassword())) {
            throw new BadRequestError("Current password is incorrect");
        }

        // Update password
        employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
        employeeRepository.save(employee);

        log.info("Password changed for employee ID: {}", employeeId);
    }
}