package com.example.supermarket.dto.response.store;

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
public class StoreTransferResponse {
    @JsonProperty("transfer_id")
    private Long transferId;

    @JsonProperty("from_warehouse_id")
    private Long fromWarehouseId;

    @JsonProperty("from_warehouse_name")
    private String fromWarehouseName;

    @JsonProperty("to_store_id")
    private Long toStoreId;

    @JsonProperty("to_store_name")
    private String toStoreName;

    @JsonProperty("transfer_date")
    private LocalDateTime transferDate;

    @JsonProperty("employee_name")
    private String employeeName;

    private String notes;

    @JsonProperty("total_quantity")
    private Integer totalQuantity;

    private List<TransferItemResponse> items;
}
