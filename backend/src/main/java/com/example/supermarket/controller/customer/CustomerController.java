package com.example.supermarket.controller.customer;

import com.example.supermarket.dto.ApiResponse;
import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.customer.CustomerSearchRequest;
import com.example.supermarket.dto.request.customer.CustomerUpdateRequest;
import com.example.supermarket.dto.response.customer.CustomerDetailResponse;
import com.example.supermarket.dto.response.customer.CustomerResponse;
import com.example.supermarket.service.CustomerServiceI;
import com.example.supermarket.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Customer", description = "Customer management APIs")
public class CustomerController {
    private final CustomerServiceI customerService;

    /**
     * Get current customer profile
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get my profile", description = "Customer can view their own profile")
    public ResponseEntity<ApiResponse<CustomerDetailResponse>> getMyProfile() {
        Long customerId = SecurityUtils.getCurrentUserId();
        CustomerDetailResponse response = customerService.getMyProfile(customerId);

        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", response));
    }

    /**
     * Update current customer profile
     */
    @PutMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Update my profile", description = "Customer can update their own profile")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateMyProfile(
            @Valid @RequestBody CustomerUpdateRequest request
            ) {
        Long customerId = SecurityUtils.getCurrentUserId();
        CustomerResponse response = customerService.updateCustomer(customerId, request);

        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    /**
     * Delete current customer account
     */
    @DeleteMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Delete my account", description = "Customer can delete their own account")
    public ResponseEntity<ApiResponse<Void>> deleteMyAccount() {
        Long customerId = SecurityUtils.getCurrentUserId();
        customerService.deleteCustomer(customerId);

        return ResponseEntity.ok(ApiResponse.success("Account deleted successfully", null));
    }

    /**
     * Search and list customers with filters
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'SALES')")
    @Operation(summary = "Search customers", description = "Manager/Sales can search and list customers")
    public ResponseEntity<ApiResponse<PageResponse<CustomerResponse>>> searchCustomers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        CustomerSearchRequest searchRequest = new CustomerSearchRequest(
                keyword, email, phone, page, size, sortBy, sortDirection
        );

        PageResponse<CustomerResponse> response = customerService.searchCustomers(searchRequest);
        return ResponseEntity.ok(ApiResponse.success("Customers retrieved successfully", response));
    }

    /**
     * Get customer by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'SALES')")
    @Operation(summary = "Get customer by ID", description = "Manager/Sales can view customer details")
    public ResponseEntity<ApiResponse<CustomerDetailResponse>> getCustomerById(
            @PathVariable Long id
    ) {
        CustomerDetailResponse response = customerService.getCustomerById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Customers retrieved successfully", response)
        );
    }

    /**
     * Update customer by ID
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'SALES')")
    @Operation(summary = "Update customer", description = "Manager/Sales can update customer information")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerUpdateRequest request
    ) {
        CustomerResponse response = customerService.updateCustomer(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Customer updated successfully", response)
        );
    }

    /**
     * Delete customer by ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete customer", description = "Manager can delete customer account")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(
            @PathVariable Long id
    ) {
        customerService.deleteCustomer(id);

        return ResponseEntity.ok(
                ApiResponse.success("Customer deleted successfully", null)
        );
    }

    // ==================== LOYALTY POINTS MANAGEMENT ====================

    /**
     * Add loyalty points to customer
     */
    @PostMapping("/{id}/loyalty-points/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'SALES')")
    @Operation(summary = "Add loyalty points", description = "Manager/Sales can add loyalty points to customer")
    public ResponseEntity<ApiResponse<CustomerResponse>> addLoyaltyPoints(
            @PathVariable Long id,
            @RequestParam Integer points
    ) {
        CustomerResponse response = customerService.addLoyaltyPoints(id, points);

        return ResponseEntity.ok(
                ApiResponse.success("Loyalty points added successfully", response)
        );
    }

    /**
     * Redeem loyalty points
     */
    @PostMapping("/{id}/loyalty-points/redeem")
    @PreAuthorize("hasAnyRole('MANAGER', 'SALES')")
    @Operation(summary = "Redeem loyalty points", description = "Manager/Sales can redeem loyalty points for customer")
    public ResponseEntity<ApiResponse<CustomerResponse>> redeemLoyaltyPoints(
            @PathVariable Long id,
            @RequestParam Integer points
    ) {
        CustomerResponse response = customerService.redeemLoyaltyPoints(id, points);

        return ResponseEntity.ok(
                ApiResponse.success("Loyalty points redeemed successfully", response)
        );
    }

    /**
     * Get top customers by spending
     */
    @GetMapping("/top")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Get top customers", description = "Manager can view top customers by spending")
    public ResponseEntity<ApiResponse<PageResponse<CustomerDetailResponse>>> getTopCustomers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        PageResponse<CustomerDetailResponse> response = customerService.getTopCustomers(page, size);

        return ResponseEntity.ok(
                ApiResponse.success("Top customers retrieved successfully", response)
        );
    }
}
