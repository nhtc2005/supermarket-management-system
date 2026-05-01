package com.example.supermarket.service;

import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.warehouse.WarehouseRequest;
import com.example.supermarket.dto.response.warehouse.WarehouseResponse;

public interface WarehouseServiceI {
    WarehouseResponse createWarehouse(WarehouseRequest request);
    PageResponse<WarehouseResponse> getAllWarehouses(int page, int size);
    WarehouseResponse getWarehouseById(Long id);
    WarehouseResponse updateWarehouse(Long id, WarehouseRequest request);
    void deleteWarehouse(Long id);
}
