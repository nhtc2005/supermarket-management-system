package com.example.supermarket.dto.response.warehouse;

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
public class ImportItemResponse {
    @JsonProperty("batch_id")
    private Long batchId;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("variant_id")
    private Long variantId;

    private Integer quantity;

    private String manufacture;

    @JsonProperty("expiry_date")
    private LocalDate expiryDate;
}
