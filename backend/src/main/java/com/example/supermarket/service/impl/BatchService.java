package com.example.supermarket.service.impl;

import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.batch.BatchRequest;
import com.example.supermarket.dto.response.batch.BatchResponse;
import com.example.supermarket.entity.Batch;
import com.example.supermarket.entity.Product;
import com.example.supermarket.entity.ProductVariant;
import com.example.supermarket.entity.Warehouse;
import com.example.supermarket.exception.NotFoundError;
import com.example.supermarket.repository.BatchRepository;
import com.example.supermarket.repository.ProductRepository;
import com.example.supermarket.repository.ProductVariantRepository;
import com.example.supermarket.repository.WarehouseRepository;
import com.example.supermarket.service.BatchServiceI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchService implements BatchServiceI {
    private final BatchRepository batchRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final WarehouseRepository warehouseRepository;

    @Transactional
    public BatchResponse createBatch(BatchRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundError("Product not found"));

        ProductVariant variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new NotFoundError("Variant not found"));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new NotFoundError("Warehouse not found"));

        Batch batch = Batch.builder()
                .productId(product.getId())
                .variantId(variant.getId())
                .warehouseId(warehouse.getId())
                .manufacture(request.getManufacture())
                .supplier(request.getSupplier())
                .quantityTotal(request.getQuantityTotal())
                .quantityAvailable(request.getQuantityTotal())
                .createDate(request.getCreateDate() != null ? request.getCreateDate() : LocalDate.now())
                .expiryDate(request.getExpiryDate())
                .build();

        batch = batchRepository.save(batch);
        return toResponse(batch);
    }

    @Transactional(readOnly = true)
    public PageResponse<BatchResponse> getAllBatches(int page, int size) {
        Page<Batch> batchPage = batchRepository.findAll(PageRequest.of(page, size));

        return PageResponse.<BatchResponse>builder()
                .content(batchPage.getContent().stream().map(this::toResponse).toList())
                .pageNumber(batchPage.getNumber())
                .pageSize(batchPage.getSize())
                .totalElements(batchPage.getTotalElements())
                .totalPages(batchPage.getTotalPages())
                .isLast(batchPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public BatchResponse getBatchById(Long id) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Batch not found"));
        return toResponse(batch);
    }

    @Transactional(readOnly = true)
    public PageResponse<BatchResponse> getExpiredBatches(int page, int size) {
        Page<Batch> batchPage = batchRepository.findByExpiryDateBefore(LocalDate.now(), PageRequest.of(page, size));

        return PageResponse.<BatchResponse>builder()
                .content(batchPage.getContent().stream().map(this::toResponse).toList())
                .pageNumber(batchPage.getNumber())
                .pageSize(batchPage.getSize())
                .totalElements(batchPage.getTotalElements())
                .totalPages(batchPage.getTotalPages())
                .isLast(batchPage.isLast())
                .build();
    }

    @Transactional
    public void deleteBatch(Long id) {
        if (!batchRepository.existsById(id)) {
            throw new NotFoundError("Batch not found");
        }
        batchRepository.deleteById(id);
    }

    private BatchResponse toResponse(Batch batch) {
        boolean isExpired = batch.getExpiryDate() != null && batch.getExpiryDate().isBefore(LocalDate.now());
        Product product = productRepository.findById(batch.getProductId())
                .orElse(null);

        Warehouse warehouse = warehouseRepository.findById(batch.getWarehouseId())
                .orElse(null);

        return BatchResponse.builder()
                .id(batch.getId())
                .productId(batch.getProductId())
                .productName(product != null ? product.getName() : null)
                .warehouseId(batch.getWarehouseId())
                .warehouseName(warehouse != null ? warehouse.getName() : null)
                .manufacture(batch.getManufacture())
                .supplier(batch.getSupplier())
                .quantityTotal(batch.getQuantityTotal())
                .quantityAvailable(batch.getQuantityAvailable())
                .createDate(batch.getCreateDate())
                .expiryDate(batch.getExpiryDate())
                .isExpired(isExpired)
                .build();
    }
}
