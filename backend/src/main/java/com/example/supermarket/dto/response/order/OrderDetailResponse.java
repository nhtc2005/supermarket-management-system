package com.example.supermarket.dto.response.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailResponse {
    private Long id;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("customer_email")
    private String customerEmail;

    @JsonProperty("customer_phone")
    private String customerPhone;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    private String status;

    @JsonProperty("total_money")
    private Double totalMoney;

    @JsonProperty("points_earned")
    private Integer pointsEarned;

    @JsonProperty("points_used")
    private Integer pointsUsed;

    private List<OrderItemResponse> items;
}
