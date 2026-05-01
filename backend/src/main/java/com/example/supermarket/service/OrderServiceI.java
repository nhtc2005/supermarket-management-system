package com.example.supermarket.service;

import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.order.OrderCreateRequest;
import com.example.supermarket.dto.request.order.OrderSearchRequest;
import com.example.supermarket.dto.request.order.OrderUpdateStatusRequest;
import com.example.supermarket.dto.response.order.OrderDetailResponse;
import com.example.supermarket.dto.response.order.OrderResponse;
import com.example.supermarket.dto.response.order.OrderStatisticsResponse;

import java.time.LocalDateTime;

public interface OrderServiceI {
    OrderDetailResponse createOrder(OrderCreateRequest request);
    OrderDetailResponse getOrderById(Long id);
    PageResponse<OrderResponse> searchOrders(OrderSearchRequest request);
    PageResponse<OrderResponse> getMyOrders(Long customerId, Integer page, Integer size);
    OrderResponse updateOrderStatus(Long id, OrderUpdateStatusRequest request);
    void cancelOrder(Long id);
    void deleteOrder(Long id);
    OrderStatisticsResponse getOrderStatistics(LocalDateTime fromDate, LocalDateTime toDate);
}
