package com.example.supermarket.dto.response.warehouse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportItemResponse {
    @JsonProperty("batch_id")
    private Long batchId;

    @JsonProperty("product_name")
    private String productName;

    private Integer quantity;
}
