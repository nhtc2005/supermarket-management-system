package com.example.supermarket.dto.response.warehouse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseExportResponse {
    @JsonProperty("details_id")
    private Long detailsId;

    @JsonProperty("warehouse_id")
    private Long warehouseId;

    @JsonProperty("warehouse_name")
    private String warehouseName;

    private String reason;

    @JsonProperty("export_date")
    private LocalDateTime exportDate;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("total_quantity")
    private Integer totalQuantity;

    private List<ExportItemResponse> items;
}
