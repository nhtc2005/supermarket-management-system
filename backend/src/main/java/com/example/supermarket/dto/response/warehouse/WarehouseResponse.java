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
public class WarehouseResponse {
    private Long id;
    private String name;
    private String location;

    @JsonProperty("manager_id")
    private Long managerId;

    @JsonProperty("manager_name")
    private String managerName;

    @JsonProperty("total_batches")
    private Integer totalBatches;
}
