package com.example.supermarket.dto.response.customer;

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
public class CustomerDetailResponse {
    private Long id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String email;
    private String phone;
    private String address;

    @JsonProperty("loyalty_points")
    private Integer loyaltyPoints;

    @JsonProperty("total_orders")
    private Long totalOrders;

    @JsonProperty("total_spent")
    private Double totalSpent;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
