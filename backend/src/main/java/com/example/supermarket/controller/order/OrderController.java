package com.example.supermarket.controller.order;

import com.example.supermarket.dto.ApiResponse;
import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.order.OrderCreateRequest;
import com.example.supermarket.dto.request.order.OrderSearchRequest;
import com.example.supermarket.dto.request.order.OrderUpdateStatusRequest;
import com.example.supermarket.dto.response.order.OrderDetailResponse;
import com.example.supermarket.dto.response.order.OrderResponse;
import com.example.supermarket.service.OrderServiceI;
import com.example.supermarket.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Order", description = "Order management APIs")
public class OrderController {
    private final OrderServiceI orderService;

    /**
     * Create new order
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SALES')")
    @Operation(summary = "Create order", description = "Customer or Sales can create new order")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> createOrder(
            @Valid @RequestBody OrderCreateRequest request
    ) {
        // If customer is creating order, use their own ID
        if (SecurityUtils.hasRole("CUSTOMER")) {
            request.setCustomerId(SecurityUtils.getCurrentUserId());
        }

        OrderDetailResponse response = orderService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Order created successfully", response)
        );
    }

    /**
     * Get my orders (for customers)
     */
    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get my orders", description = "Customer can view their own orders")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Long customerId = SecurityUtils.getCurrentUserId();
        PageResponse<OrderResponse> response = orderService.getMyOrders(customerId, page, size);

        return ResponseEntity.ok(
                ApiResponse.success("Orders retrieved successfully", response)
        );
    }

    /**
     * Search and filter orders
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('SALES', 'WAREHOUSE', 'MANAGER')")
    @Operation(summary = "Search orders", description = "Staff can search and filter orders")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> searchOrders(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        OrderSearchRequest searchRequest = new OrderSearchRequest(
                customerId, status, fromDate, toDate, page, size, sortBy, sortDirection
        );

        PageResponse<OrderResponse> response = orderService.searchOrders(searchRequest);

        return ResponseEntity.ok(
                ApiResponse.success("Orders retrieved successfully", response)
        );
    }

    /**
     * Get order by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SALES', 'WAREHOUSE', 'MANAGER')")
    @Operation(summary = "Get order by ID", description = "View order details")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderById(
            @PathVariable Long id
    ) {
        OrderDetailResponse response = orderService.getOrderById(id);

        // If customer, verify they own this order
        if (SecurityUtils.hasRole("CUSTOMER")) {
            SecurityUtils.verifyResourceOwnership(response.getCustomerId());
        }

        return ResponseEntity.ok(
                ApiResponse.success("Orders retrieved successfully", response)
        );
    }

    /**
     * Update order status
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SALES', 'MANAGER')")
    @Operation(summary = "Update order status", description = "Sales/Manager can update order status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderUpdateStatusRequest request
    ) {
        OrderResponse response = orderService.updateOrderStatus(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Order status updated successfully", response)
        );
    }

    /**
     * Cancel order
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SALES', 'MANAGER')")
    @Operation(summary = "Cancel order", description = "Cancel a pending order")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable Long id
    ) {
        // If customer, verify they own this order
        if (SecurityUtils.hasRole("CUSTOMER")) {
            OrderDetailResponse order = orderService.getOrderById(id);
            SecurityUtils.verifyResourceOwnership(order.getCustomerId());
        }

        orderService.cancelOrder(id);

        return ResponseEntity.ok(
                ApiResponse.success("Order cancelled successfully", null)
        );
    }

    /**
     * Delete order
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete order", description = "Manager can delete order")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @PathVariable Long id
    ) {
        orderService.deleteOrder(id);

        return ResponseEntity.ok(
                ApiResponse.success("Order deleted successfully", null)
        );
    }

    /**
     * Get order statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Get order statistics", description = "Manager can view order statistics")
    public ResponseEntity<ApiResponse<Object>> getOrderStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate
    ) {
        Object response = orderService.getOrderStatistics(fromDate, toDate);

        return ResponseEntity.ok(
                ApiResponse.success("Statistics retrieved successfully", response)
        );
    }
}
