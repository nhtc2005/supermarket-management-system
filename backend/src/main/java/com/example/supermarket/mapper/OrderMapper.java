package com.example.supermarket.mapper;

import com.example.supermarket.dto.response.order.OrderDetailResponse;
import com.example.supermarket.dto.response.order.OrderResponse;
import com.example.supermarket.entity.Order;
import com.example.supermarket.entity.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "pointsEarned", source = "pointsEarned")
    @Mapping(target = "pointsUsed", source = "pointsUsed")
    OrderDetailResponse toDetailResponse(
            Order order,
            List<OrderDetail> orderDetails,
            Integer pointsEarned,
            Integer pointsUsed
    );

    @Mapping(target = "totalItems", source = "totalItems")
    @Mapping(target = "pointsEarned", source = "pointsEarned")
    OrderResponse toResponse(Order order, Integer totalItems, Integer pointsEarned);
}
