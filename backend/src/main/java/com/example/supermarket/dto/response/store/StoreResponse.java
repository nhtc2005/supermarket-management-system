package com.example.supermarket.dto.response.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreResponse {
    private Long id;
    private String name;
    private String location;

    @JsonProperty("manager_id")
    private Long managerId;

    @JsonProperty("manager_name")
    private String managerName;

    @JsonProperty("total_products")
    private Integer totalProducts;
}
