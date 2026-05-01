package com.example.supermarket.dto.response.warehouse;

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
public class WarehouseImportResponse {
    @JsonProperty("details_id")
    private Long detailsId;

    @JsonProperty("warehouse_id")
    private Long warehouseId;

    @JsonProperty("warehouse_name")
    private String warehouseName;

    private String supplier;

    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    @JsonProperty("import_date")
    private LocalDateTime importDate;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("total_quantity")
    private Integer totalQuantity;

    @JsonProperty("total_value")
    private BigDecimal totalValue;

    private List<ImportItemResponse> items;
}
