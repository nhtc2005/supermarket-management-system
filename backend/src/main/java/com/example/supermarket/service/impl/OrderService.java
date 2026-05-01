package com.example.supermarket.service.impl;

import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.order.OrderCreateRequest;
import com.example.supermarket.dto.request.order.OrderSearchRequest;
import com.example.supermarket.dto.request.order.OrderUpdateStatusRequest;
import com.example.supermarket.dto.response.order.OrderDetailResponse;
import com.example.supermarket.dto.response.order.OrderResponse;
import com.example.supermarket.dto.response.order.OrderStatisticsResponse;
import com.example.supermarket.entity.*;
import com.example.supermarket.entity.compositePk.OrderDetailID;
import com.example.supermarket.entity.compositePk.ProductStoreID;
import com.example.supermarket.exception.BadRequestError;
import com.example.supermarket.exception.NotFoundError;
import com.example.supermarket.mapper.OrderMapper;
import com.example.supermarket.repository.*;
import com.example.supermarket.service.OrderServiceI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements OrderServiceI {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductStoreRepository productStoreRepository;
    private final OrderMapper orderMapper;

    private static final int POINTS_PER_DOLLAR = 1; // 1 point per dollar spent
    private static final double POINT_VALUE = 0.01; // 1 point = $0.01

    /**
     * Create new order with inventory management
     */
    @Transactional
    public OrderDetailResponse createOrder(OrderCreateRequest request) {
        log.info("Creating new order for customer ID: {}", request.getCustomerId());

        // Validate customer
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new NotFoundError("Customer not found"));

        // Validate product availability and calculate total
        double totalMoney = 0.0;
        List<OrderDetail> orderDetails = new ArrayList<>();
        Map<Long, Integer> productQuantityMap = new HashMap<>();

        for (var itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new NotFoundError("Product not found with ID: " + itemRequest.getProductId()));

            // Check if we need to specify storeId (for now, we'll use store 1 as default)
            // In production, this should come from the request or user's location
            Long defaultStoreId = 1L;

            // Check product availability in store
            ProductStoreID productStoreId = ProductStoreID.builder()
                    .productId(product.getId())
                    .storeId(defaultStoreId)
                    .build();

            ProductStore productStore = productStoreRepository.findById(productStoreId)
                    .orElseThrow(() -> new BadRequestError("Product not available in store"));

            // Check if enough stock
            if (productStore.getQuantityInStock() < itemRequest.getQuantity()) {
                throw new BadRequestError(
                        "Insufficient stock for product: " + product.getName() +
                                ". Available: " + productStore.getQuantityInStock() +
                                ", Requested: " + itemRequest.getQuantity()
                );
            }

            // Calculate subtotal
            float subTotal = product.getPrice().floatValue() * itemRequest.getQuantity();
            totalMoney += subTotal;

            // Store for later inventory update
            productQuantityMap.put(product.getId(), itemRequest.getQuantity());

            // Create order detail (will set order later)
            OrderDetail orderDetail = OrderDetail.builder()
                    .id(new OrderDetailID())
                    .quantity(itemRequest.getQuantity())
                    .subtotal(subTotal)
                    .build();
            orderDetail.setProductId(product.getId());

            orderDetails.add(orderDetail);
        }

        // Apply loyalty points discount if requested
        Integer pointsUsed = 0;
        if (request.getUsePoints() && customer.getLoyaltyPoints() > 0) {
            double discount = customer.getLoyaltyPoints() * POINT_VALUE;
            if (discount > totalMoney) {
                discount = totalMoney;
            }
            totalMoney -= discount;
            pointsUsed = (int)(discount / POINT_VALUE);

            // Deduct points from customer
            customer.setLoyaltyPoints(customer.getLoyaltyPoints() - pointsUsed);
        }

        // Create order
        Order order = Order.builder()
                .customerId(customer.getId())
                .createdAt(LocalDateTime.now())
                .status("Completed")
                .totalMoney(totalMoney)
                .build();

        order = orderRepository.save(order);

        // Save order details with order reference
        for (OrderDetail detail : orderDetails) {
            detail.getId().setOrderID(order.getId());
            orderDetailRepository.save(detail);
        }

        // *** CRITICAL: REDUCE INVENTORY IMMEDIATELY ***
        Long defaultStoreId = 1L;
        for (Map.Entry<Long, Integer> entry : productQuantityMap.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            ProductStoreID productStoreId = ProductStoreID.builder()
                    .productId(productId)
                    .storeId(defaultStoreId)
                    .build();

            ProductStore productStore = productStoreRepository.findById(productStoreId).get();
            productStore.setQuantityInStock(productStore.getQuantityInStock() - quantity);
            productStoreRepository.save(productStore);

            log.info("Reduced inventory: Product {} by {} units. Remaining: {}",
                    productId, quantity, productStore.getQuantityInStock());
        }

        // Calculate and add loyalty points earned
        Integer pointsEarned = (int)(totalMoney * POINTS_PER_DOLLAR);
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + pointsEarned);
        customerRepository.save(customer);

        log.info("Order created successfully with ID: {}. Inventory updated.", order.getId());

        return orderMapper.toDetailResponse(order, orderDetails, pointsEarned, pointsUsed);
    }

    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderById(Long id) {
        log.info("Getting order with ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Order not found with ID: " + id));

        List<OrderDetail> orderDetails = orderDetailRepository.findByIdOrderID(id);

        Integer pointsEarned = (int)(order.getTotalMoney() * POINTS_PER_DOLLAR);

        return orderMapper.toDetailResponse(order, orderDetails, pointsEarned, 0);
    }

    /**
     * Search orders with filters
     */
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> searchOrders(OrderSearchRequest request) {
        log.info("Searching orders with filters");

        Sort sort = request.getSortDirection().equalsIgnoreCase("DESC")
                ? Sort.by(request.getSortBy()).descending()
                : Sort.by(request.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Order> orderPage;

        if (request.getCustomerId() != null && request.getStatus() != null) {
            orderPage = orderRepository.findByCustomerIdAndStatus(
                    request.getCustomerId(),
                    request.getStatus(),
                    pageable
            );
        } else if (request.getCustomerId() != null) {
            orderPage = orderRepository.findByCustomerId(request.getCustomerId(), pageable);
        } else if (request.getStatus() != null) {
            orderPage = orderRepository.findByStatus(request.getStatus(), pageable);
        } else if (request.getFromDate() != null && request.getToDate() != null) {
            orderPage = orderRepository.findByCreatedAtBetween(
                    request.getFromDate(),
                    request.getToDate(),
                    pageable
            );
        } else {
            orderPage = orderRepository.findAll(pageable);
        }

        List<OrderResponse> content = orderPage.getContent().stream()
                .map(order -> {
                    Long totalItems = orderDetailRepository.countByIdOrderID(order.getId());
                    Integer pointsEarned = (int)(order.getTotalMoney() * POINTS_PER_DOLLAR);
                    Customer customer = customerRepository.findById(order.getCustomerId()).orElse(null);
                    String customerName = customer != null ? customer.getFirstName() + " " + customer.getLastName() : "Unknown";

                    return OrderResponse.builder()
                            .id(order.getId())
                            .customerId(order.getCustomerId())
                            .customerName(customerName)
                            .createdAt(order.getCreatedAt())
                            .status(order.getStatus())
                            .totalMoney(order.getTotalMoney())
                            .totalItems(totalItems.intValue())
                            .pointsEarned(pointsEarned)
                            .build();
                })
                .toList();

        return PageResponse.<OrderResponse>builder()
                .content(content)
                .pageNumber(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .isLast(orderPage.isLast())
                .build();
    }

    /**
     * Get customer's orders
     */
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getMyOrders(Long customerId, Integer page, Integer size) {
        log.info("Getting orders for customer ID: {}", customerId);

        OrderSearchRequest searchRequest = new OrderSearchRequest();
        searchRequest.setCustomerId(customerId);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy("createdAt");
        searchRequest.setSortDirection("DESC");

        return searchOrders(searchRequest);
    }

    /**
     * Update order status with inventory restoration on cancel
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderUpdateStatusRequest request) {
        log.info("Updating order status for ID: {} to {}", id, request.getStatus());

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Order not found with ID: " + id));

        String currentStatus = order.getStatus();
        String newStatus = request.getStatus();

        // Validate status transition
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new BadRequestError("Invalid status transition from " + currentStatus + " to " + newStatus);
        }

        // *** RESTORE INVENTORY IF CANCELLED ***
        if (newStatus.equals("Cancelled") && !currentStatus.equals("Cancelled")) {
            restoreInventory(order.getId());
            log.info("Inventory restored for cancelled order: {}", id);
        }

        order.setStatus(newStatus);
        order = orderRepository.save(order);

        log.info("Order status updated successfully");

        Long totalItems = orderDetailRepository.countByIdOrderID(id);
        Integer pointsEarned = (int)(order.getTotalMoney() * POINTS_PER_DOLLAR);
        Customer customer = customerRepository.findById(order.getCustomerId()).orElse(null);
        String customerName = customer != null ? customer.getFirstName() + " " + customer.getLastName() : "Unknown";

        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .customerName(customerName)
                .createdAt(order.getCreatedAt())
                .status(order.getStatus())
                .totalMoney(order.getTotalMoney())
                .totalItems(totalItems.intValue())
                .pointsEarned(pointsEarned)
                .build();
    }

    /**
     * Cancel order and restore inventory
     */
    @Transactional
    public void cancelOrder(Long id) {
        log.info("Cancelling order with ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Order not found with ID: " + id));

        if (!order.getStatus().equals("Pending")) {
            throw new BadRequestError("Only pending orders can be cancelled");
        }

        // Restore inventory
        restoreInventory(order.getId());

        // Update status
        order.setStatus("Cancelled");
        orderRepository.save(order);

        log.info("Order cancelled and inventory restored successfully");
    }

    /**
     * Delete order (soft delete)
     */
    @Transactional
    public void deleteOrder(Long id) {
        log.info("Deleting order with ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Order not found with ID: " + id));

        if (order.getStatus().equals("Processing") || order.getStatus().equals("Completed")) {
            throw new BadRequestError("Cannot delete order with status: " + order.getStatus());
        }

        // If order was pending, restore inventory before delete
        if (order.getStatus().equals("Pending")) {
            restoreInventory(order.getId());
        }

        orderRepository.delete(order);
        log.info("Order deleted successfully");
    }

    /**
     * Restore inventory when order is cancelled
     */
    private void restoreInventory(Long orderId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByIdOrderID(orderId);
        Long defaultStoreId = 1L; // Should match the store used during order creation

        for (OrderDetail detail : orderDetails) {
            ProductStoreID productStoreId = ProductStoreID.builder()
                    .productId(detail.getProductId())
                    .storeId(defaultStoreId)
                    .build();

            ProductStore productStore = productStoreRepository.findById(productStoreId)
                    .orElseThrow(() -> new NotFoundError("Product store not found"));

            // Add back the quantity
            productStore.setQuantityInStock(productStore.getQuantityInStock() + detail.getQuantity());
            productStoreRepository.save(productStore);

            log.info("Restored inventory: Product {} by {} units. New quantity: {}",
                    detail.getProductId(), detail.getQuantity(), productStore.getQuantityInStock());
        }
    }

    /**
     * Validate status transition
     */
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        return switch (currentStatus) {
            case "Pending" -> newStatus.equals("Processing") || newStatus.equals("Cancelled");
            case "Processing" -> newStatus.equals("Completed") || newStatus.equals("Cancelled");
            default -> false;
        };
    }

    /**
     * Get order statistics
     */
    @Transactional(readOnly = true)
    public OrderStatisticsResponse getOrderStatistics(LocalDateTime fromDate, LocalDateTime toDate) {
        log.info("Getting order statistics from {} to {}", fromDate, toDate);

        Long totalOrders = orderRepository.countByCreatedAtBetween(fromDate, toDate);
        Double totalRevenue = orderRepository.sumTotalMoneyByCreatedAtBetween(fromDate, toDate);

        Long pendingOrders = orderRepository.countByStatusAndCreatedAtBetween("Pending", fromDate, toDate);
        Long completedOrders = orderRepository.countByStatusAndCreatedAtBetween("Completed", fromDate, toDate);
        Long cancelledOrders = orderRepository.countByStatusAndCreatedAtBetween("Cancelled", fromDate, toDate);

        return OrderStatisticsResponse.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue != null ? totalRevenue : 0.0)
                .pendingOrders(pendingOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .averageOrderValue(totalOrders > 0 ? (totalRevenue != null ? totalRevenue : 0.0) / totalOrders : 0.0)
                .build();
    }
}
