package com.example.supermarket.service;

import com.example.supermarket.dto.request.store.StoreTransferRequest;
import com.example.supermarket.dto.request.warehouse.WarehouseExportRequest;
import com.example.supermarket.dto.request.warehouse.WarehouseImportRequest;
import com.example.supermarket.dto.response.store.StoreTransferResponse;
import com.example.supermarket.dto.response.warehouse.WarehouseExportResponse;
import com.example.supermarket.dto.response.warehouse.WarehouseImportResponse;

import java.util.List;

public interface WarehouseOperationsServiceI {
    WarehouseImportResponse importProducts(WarehouseImportRequest request);
    WarehouseExportResponse exportProducts(WarehouseExportRequest request);
    StoreTransferResponse transferToStore(StoreTransferRequest request);
    List<WarehouseImportResponse> getImportHistory(Long warehouseId);
    List<WarehouseExportResponse> getExportHistory(Long warehouseId);
}
