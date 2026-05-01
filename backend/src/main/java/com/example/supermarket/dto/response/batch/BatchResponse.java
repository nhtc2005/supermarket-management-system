package com.example.supermarket.dto.response.batch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchResponse {
    private Long id;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("warehouse_id")
    private Long warehouseId;

    @JsonProperty("warehouse_name")
    private String warehouseName;

    private String manufacture;
    private String supplier;

    @JsonProperty("quantity_total")
    private Integer quantityTotal;

    @JsonProperty("quantity_available")
    private Integer quantityAvailable;

    @JsonProperty("create_date")
    private LocalDate createDate;

    @JsonProperty("expiry_date")
    private LocalDate expiryDate;

    @JsonProperty("is_expired")
    private Boolean isExpired;
}
