package com.example.supermarket.dto.response.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    private String status;

    @JsonProperty("total_money")
    private Double totalMoney;

    @JsonProperty("total_items")
    private Integer totalItems;

    @JsonProperty("points_earned")
    private Integer pointsEarned;
}
