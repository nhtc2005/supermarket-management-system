package com.example.supermarket.service.impl;

import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.warehouse.WarehouseRequest;
import com.example.supermarket.dto.response.warehouse.WarehouseResponse;
import com.example.supermarket.entity.Warehouse;
import com.example.supermarket.exception.NotFoundError;
import com.example.supermarket.repository.BatchRepository;
import com.example.supermarket.repository.WarehouseRepository;
import com.example.supermarket.service.WarehouseServiceI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseService implements WarehouseServiceI {
    private final WarehouseRepository warehouseRepository;
    private final BatchRepository batchRepository;

    @Transactional
    public WarehouseResponse createWarehouse(WarehouseRequest request) {
        Warehouse warehouse = Warehouse.builder()
                .name(request.getName())
                .location(request.getLocation())
                .build();

        warehouse = warehouseRepository.save(warehouse);
        return toResponse(warehouse);
    }

    @Transactional(readOnly = true)
    public PageResponse<WarehouseResponse> getAllWarehouses(int page, int size) {
        Page<Warehouse> warehousePage = warehouseRepository.findAll(PageRequest.of(page, size));

        return PageResponse.<WarehouseResponse>builder()
                .content(warehousePage.getContent().stream().map(this::toResponse).toList())
                .pageNumber(warehousePage.getNumber())
                .pageSize(warehousePage.getSize())
                .totalElements(warehousePage.getTotalElements())
                .totalPages(warehousePage.getTotalPages())
                .isLast(warehousePage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public WarehouseResponse getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Warehouse not found"));
        return toResponse(warehouse);
    }

    @Transactional
    public WarehouseResponse updateWarehouse(Long id, WarehouseRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Warehouse not found"));

        warehouse.setName(request.getName());
        warehouse.setLocation(request.getLocation());
        warehouse = warehouseRepository.save(warehouse);
        return toResponse(warehouse);
    }

    @Transactional
    public void deleteWarehouse(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new NotFoundError("Warehouse not found");
        }
        warehouseRepository.deleteById(id);
    }

    private WarehouseResponse toResponse(Warehouse warehouse) {
        Integer totalBatches = batchRepository.findByWarehouseId(warehouse.getId()).size();
        return WarehouseResponse.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .location(warehouse.getLocation())
                .totalBatches(totalBatches)
                .build();
    }
}
