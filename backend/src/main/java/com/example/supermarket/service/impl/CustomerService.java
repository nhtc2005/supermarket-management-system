package com.example.supermarket.service.impl;

import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.customer.CustomerSearchRequest;
import com.example.supermarket.dto.request.customer.CustomerUpdateRequest;
import com.example.supermarket.dto.response.customer.CustomerDetailResponse;
import com.example.supermarket.dto.response.customer.CustomerResponse;
import com.example.supermarket.entity.Customer;
import com.example.supermarket.exception.BadRequestError;
import com.example.supermarket.exception.ConflictError;
import com.example.supermarket.exception.NotFoundError;
import com.example.supermarket.mapper.CustomerMapper;
import com.example.supermarket.repository.CustomerRepository;
import com.example.supermarket.repository.OrderRepository;
import com.example.supermarket.service.CustomerServiceI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService implements CustomerServiceI {
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get customer by ID (for Manager/Sales)
     */
    @Transactional(readOnly = true)
    public CustomerDetailResponse getCustomerById(Long id) {
        log.info("Getting customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Customer not found with ID: " + id));

        // Get additional statistics
        Long totalOrders = orderRepository.countByCustomerId(id);
        Double totalSpent = orderRepository.sumTotalMoneyByCustomerId(id);

        return customerMapper.toDetailResponse(customer, totalOrders, totalSpent != null ? totalSpent : 0.0);
    }

    /**
     * Get current customer profile (for Customer themselves)
     */
    @Transactional(readOnly = true)
    public CustomerDetailResponse getMyProfile(Long customerId) {
        log.info("Getting profile for customer ID: {}", customerId);
        return getCustomerById(customerId);
    }

    /**
     * Search and filter customers with pagination
     */
    @Transactional(readOnly = true)
    public PageResponse<CustomerResponse> searchCustomers(CustomerSearchRequest request) {
        log.info("Searching customers with filters: {}", request);

        // Create pageable
        Sort sort = request.getSortDirection().equalsIgnoreCase("DESC")
                ? Sort.by(request.getSortBy()).descending()
                : Sort.by(request.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        // Search based on filters
        Page<Customer> customerPage;

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            customerPage = customerRepository.findByEmailContaining(request.getEmail(), pageable);
        } else if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            customerPage = customerRepository.findByPhoneContaining(request.getPhone(), pageable);
        } else if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            customerPage = customerRepository.searchByKeyword(request.getKeyword(), pageable);
        } else {
            customerPage = customerRepository.findAll(pageable);
        }

        return PageResponse.<CustomerResponse>builder()
                .content(customerPage.getContent().stream()
                        .map(customerMapper::toResponse)
                        .toList())
                .pageNumber(customerPage.getNumber())
                .pageSize(customerPage.getSize())
                .totalElements(customerPage.getTotalElements())
                .totalPages(customerPage.getTotalPages())
                .isLast(customerPage.isLast())
                .build();
    }

    /**
     * Update customer profile
     */
    @Transactional
    public CustomerResponse updateCustomer(Long customerId, CustomerUpdateRequest request) {
        log.info("Updating customer with ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundError("Customer not found with ID: " + customerId));

        // Check email uniqueness if changed
        if (request.getEmail() != null && !request.getEmail().equals(customer.getEmail())) {
            if (customerRepository.existsByEmail(request.getEmail())) {
                throw new ConflictError("Email already exists");
            }
            customer.setEmail(request.getEmail());
        }

        // Update fields if provided
        if (request.getFirstName() != null) {
            customer.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            customer.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            customer.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            customer.setAddress(request.getAddress());
        }

        customer = customerRepository.save(customer);
        log.info("Customer updated successfully: {}", customerId);

        return customerMapper.toResponse(customer);
    }

    /**
     * Soft delete customer (set inactive or mark as deleted)
     */
    @Transactional
    public void deleteCustomer(Long customerId) {
        log.info("Deleting customer with ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundError("Customer not found with ID: " + customerId));

        // Check if customer has pending orders
        Long pendingOrders = orderRepository.countByCustomerIdAndStatus(customerId, "Pending");
        if (pendingOrders > 0) {
            throw new BadRequestError("Cannot delete customer with pending orders");
        }

        // Soft delete: just remove from repository or mark as deleted
        customerRepository.delete(customer);
        log.info("Customer deleted successfully: {}", customerId);
    }

    /**
     * Add loyalty points to customer
     */
    @Transactional
    public CustomerResponse addLoyaltyPoints(Long customerId, Integer points) {
        log.info("Adding {} loyalty points to customer ID: {}", points, customerId);

        if (points < 0) {
            throw new BadRequestError("Points must be positive");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundError("Customer not found with ID: " + customerId));

        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + points);
        customer = customerRepository.save(customer);

        log.info("Loyalty points added successfully");
        return customerMapper.toResponse(customer);
    }

    /**
     * Redeem loyalty points
     */
    @Transactional
    public CustomerResponse redeemLoyaltyPoints(Long customerId, Integer points) {
        log.info("Redeeming {} loyalty points from customer ID: {}", points, customerId);

        if (points < 0) {
            throw new BadRequestError("Points must be positive");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundError("Customer not found with ID: " + customerId));

        if (customer.getLoyaltyPoints() < points) {
            throw new BadRequestError("Insufficient loyalty points");
        }

        customer.setLoyaltyPoints(customer.getLoyaltyPoints() - points);
        customer = customerRepository.save(customer);

        log.info("Loyalty points redeemed successfully");
        return customerMapper.toResponse(customer);
    }

    /**
     * Get top customers by spending
     */
    @Transactional(readOnly = true)
    public PageResponse<CustomerDetailResponse> getTopCustomers(Integer page, Integer size) {
        log.info("Getting top customers");

        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerPage = customerRepository.findAll(pageable);

        return PageResponse.<CustomerDetailResponse>builder()
                .content(customerPage.getContent().stream()
                        .map(customer -> {
                            Long totalOrders = orderRepository.countByCustomerId(customer.getId());
                            Double totalSpent = orderRepository.sumTotalMoneyByCustomerId(customer.getId());
                            return customerMapper.toDetailResponse(customer, totalOrders, totalSpent != null ? totalSpent : 0.0);
                        })
                        .sorted((c1, c2) -> Double.compare(c2.getTotalSpent(), c1.getTotalSpent()))
                        .toList())
                .pageNumber(customerPage.getNumber())
                .pageSize(customerPage.getSize())
                .totalElements(customerPage.getTotalElements())
                .totalPages(customerPage.getTotalPages())
                .isLast(customerPage.isLast())
                .build();
    }
}
