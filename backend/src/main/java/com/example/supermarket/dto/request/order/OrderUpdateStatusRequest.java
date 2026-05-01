package com.example.supermarket.dto.request.order;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateStatusRequest {
    @NotBlank(message = "Status is required")
    private String status; // Pending, Processing, Completed, Cancelled
}
