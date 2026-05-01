package com.example.supermarket.service.impl;

import com.example.supermarket.dto.request.store.StoreTransferRequest;
import com.example.supermarket.dto.request.store.TransferItemRequest;
import com.example.supermarket.dto.request.warehouse.ExportItemRequest;
import com.example.supermarket.dto.request.warehouse.ImportItemRequest;
import com.example.supermarket.dto.request.warehouse.WarehouseExportRequest;
import com.example.supermarket.dto.request.warehouse.WarehouseImportRequest;
import com.example.supermarket.dto.response.store.StoreTransferResponse;
import com.example.supermarket.dto.response.store.TransferItemResponse;
import com.example.supermarket.dto.response.warehouse.ExportItemResponse;
import com.example.supermarket.dto.response.warehouse.ImportItemResponse;
import com.example.supermarket.dto.response.warehouse.WarehouseExportResponse;
import com.example.supermarket.dto.response.warehouse.WarehouseImportResponse;
import com.example.supermarket.entity.*;
import com.example.supermarket.entity.compositePk.ProductStoreID;
import com.example.supermarket.exception.BadRequestError;
import com.example.supermarket.exception.NotFoundError;
import com.example.supermarket.repository.*;
import com.example.supermarket.service.WarehouseOperationsServiceI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseOperationsService implements WarehouseOperationsServiceI {
    private final WarehouseRepository warehouseRepository;
    private final StoreRepository storeRepository;
    private final EmployeeRepository employeeRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final BatchRepository batchRepository;
    private final WarehouseImportDetailRepository importDetailRepository;
    private final WarehouseExportDetailRepository exportDetailRepository;
    private final BatchImportedRepository batchImportedRepository;
    private final BatchExportedRepository batchExportedRepository;
    private final ProductStoreRepository productStoreRepository;

    /**
     * Import products into warehouse
     */
    @Transactional
    public WarehouseImportResponse importProducts(WarehouseImportRequest request) {
        log.info("Starting import process for warehouse: {}", request.getWarehouseId());

        // Validate warehouse
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new NotFoundError("Warehouse not found"));

        // Validate employee
        Employee employee = employeeRepository.findById(request.getEmployeeImportId())
                .orElseThrow(() -> new NotFoundError("Employee not found"));

        // Create import detail record
        WarehouseImportDetail importDetail = WarehouseImportDetail.builder()
                .warehouseID(warehouse.getId())
                .supplier(request.getSupplier())
                .unitPrice(request.getUnitPrice().floatValue())
                .employeeImportId(employee.getId())
                .importDate(LocalDateTime.now())
                .build();

        importDetail = importDetailRepository.save(importDetail);

        List<ImportItemResponse> itemResponses = new ArrayList<>();
        int totalQuantity = 0;

        // Process each import item
        for (ImportItemRequest item : request.getItems()) {
            // Validate product and variant
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new NotFoundError("Product not found: " + item.getProductId()));

            ProductVariant variant = productVariantRepository.findById(item.getVariantId())
                    .orElseThrow(() -> new NotFoundError("Variant not found: " + item.getVariantId()));

            // Create new batch
            Batch batch = Batch.builder()
                    .productId(product.getId())
                    .variantId(variant.getId())
                    .warehouseId(warehouse.getId())
                    .manufacture(item.getManufacture())
                    .supplier(request.getSupplier())
                    .quantityTotal(item.getQuantity())
                    .quantityAvailable(item.getQuantity())
                    .createDate(LocalDate.now())
                    .expiryDate(item.getExpiryDate())
                    .build();

            batch = batchRepository.save(batch);

            // Link batch to import detail
            BatchImported batchImported = BatchImported.builder()
                    .batchImportId(batch.getId())
                    .importDetailsId(importDetail.getDetailsID())
                    .quantity(item.getQuantity())
                    .build();

            batchImportedRepository.save(batchImported);

            // Add to response
            itemResponses.add(ImportItemResponse.builder()
                    .batchId(batch.getId())
                    .productId(product.getId())
                    .productName(product.getName())
                    .variantId(variant.getId())
                    .quantity(item.getQuantity())
                    .manufacture(item.getManufacture())
                    .expiryDate(item.getExpiryDate())
                    .build());

            totalQuantity += item.getQuantity();
        }

        BigDecimal totalValue = request.getUnitPrice().multiply(BigDecimal.valueOf(totalQuantity));

        log.info("Import completed. Total items: {}, Total quantity: {}", itemResponses.size(), totalQuantity);

        return WarehouseImportResponse.builder()
                .detailsId(importDetail.getDetailsID())
                .warehouseId(warehouse.getId())
                .warehouseName(warehouse.getName())
                .supplier(request.getSupplier())
                .unitPrice(request.getUnitPrice())
                .importDate(importDetail.getImportDate())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .totalQuantity(totalQuantity)
                .totalValue(totalValue)
                .items(itemResponses)
                .build();
    }

    /**
     * Export products from warehouse
     */
    @Transactional
    public WarehouseExportResponse exportProducts(WarehouseExportRequest request) {
        log.info("Starting export process for warehouse: {}", request.getWarehouseId());

        // Validate warehouse
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new NotFoundError("Warehouse not found"));

        // Validate employee
        Employee employee = employeeRepository.findById(request.getEmployeeExportId())
                .orElseThrow(() -> new NotFoundError("Employee not found"));

        // Validate batches and check availability
        for (ExportItemRequest item : request.getItems()) {
            Batch batch = batchRepository.findById(item.getBatchId())
                    .orElseThrow(() -> new NotFoundError("Batch not found: " + item.getBatchId()));

            if (!batch.getWarehouseId().equals(warehouse.getId())) {
                throw new BadRequestError("Batch " + item.getBatchId() + " is not in this warehouse");
            }

            if (batch.getQuantityAvailable() < item.getQuantity()) {
                throw new BadRequestError("Insufficient quantity for batch " + item.getBatchId()
                        + ". Available: " + batch.getQuantityAvailable() + ", Requested: " + item.getQuantity());
            }
        }

        // Create export detail record
        WarehouseExportDetail exportDetail = WarehouseExportDetail.builder()
                .warehouseID(warehouse.getId())
                .reason(request.getReason())
                .employeeExportId(employee.getId())
                .exportDate(LocalDateTime.now())
                .build();

        exportDetail = exportDetailRepository.save(exportDetail);

        List<ExportItemResponse> itemResponses = new ArrayList<>();
        int totalQuantity = 0;

        // Process each export item
        for (ExportItemRequest item : request.getItems()) {
            Batch batch = batchRepository.findById(item.getBatchId()).get();

            // Reduce batch quantity
            batch.setQuantityAvailable(batch.getQuantityAvailable() - item.getQuantity());
            batchRepository.save(batch);

            // Link batch to export detail
            BatchExported batchExported = BatchExported.builder()
                    .batchExportId(batch.getId())
                    .exportDetailsId(exportDetail.getDetailsID())
                    .quantity(item.getQuantity())
                    .build();

            batchExportedRepository.save(batchExported);

            // Get product name
            Product product = productRepository.findById(batch.getProductId()).get();

            itemResponses.add(ExportItemResponse.builder()
                    .batchId(batch.getId())
                    .productName(product.getName())
                    .quantity(item.getQuantity())
                    .build());

            totalQuantity += item.getQuantity();
        }

        log.info("Export completed. Total items: {}, Total quantity: {}", itemResponses.size(), totalQuantity);

        return WarehouseExportResponse.builder()
                .detailsId(exportDetail.getDetailsID())
                .warehouseId(warehouse.getId())
                .warehouseName(warehouse.getName())
                .reason(request.getReason())
                .exportDate(exportDetail.getExportDate())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .totalQuantity(totalQuantity)
                .items(itemResponses)
                .build();
    }

    /**
     * Transfer products from warehouse to store
     */
    @Transactional
    public StoreTransferResponse transferToStore(StoreTransferRequest request) {
        log.info("Starting transfer from warehouse {} to store {}",
                request.getFromWarehouseId(), request.getToStoreId());

        // Validate warehouse
        Warehouse warehouse = warehouseRepository.findById(request.getFromWarehouseId())
                .orElseThrow(() -> new NotFoundError("Warehouse not found"));

        // Validate store
        Store store = storeRepository.findById(request.getToStoreId())
                .orElseThrow(() -> new NotFoundError("Store not found"));

        // Validate employee
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new NotFoundError("Employee not found"));

        // Validate batches
        for (TransferItemRequest item : request.getItems()) {
            Batch batch = batchRepository.findById(item.getBatchId())
                    .orElseThrow(() -> new NotFoundError("Batch not found: " + item.getBatchId()));

            if (!batch.getWarehouseId().equals(warehouse.getId())) {
                throw new BadRequestError("Batch " + item.getBatchId() + " is not in this warehouse");
            }

            if (batch.getQuantityAvailable() < item.getQuantity()) {
                throw new BadRequestError("Insufficient quantity for batch " + item.getBatchId());
            }
        }

        // Create export record for warehouse
        WarehouseExportDetail exportDetail = WarehouseExportDetail.builder()
                .warehouseID(warehouse.getId())
                .reason("Transfer to Store: " + store.getName() + (request.getNotes() != null ? " - " + request.getNotes() : ""))
                .employeeExportId(employee.getId())
                .exportDate(LocalDateTime.now())
                .build();

        exportDetail = exportDetailRepository.save(exportDetail);

        List<TransferItemResponse> itemResponses = new ArrayList<>();
        int totalQuantity = 0;

        // Process each transfer item
        for (TransferItemRequest item : request.getItems()) {
            Batch batch = batchRepository.findById(item.getBatchId()).get();
            Product product = productRepository.findById(batch.getProductId()).get();

            // Reduce warehouse batch quantity
            batch.setQuantityAvailable(batch.getQuantityAvailable() - item.getQuantity());
            batchRepository.save(batch);

            // Record export from warehouse
            BatchExported batchExported = BatchExported.builder()
                    .batchExportId(batch.getId())
                    .exportDetailsId(exportDetail.getDetailsID())
                    .quantity(item.getQuantity())
                    .build();
            batchExportedRepository.save(batchExported);

            // Update or create store inventory
            ProductStoreID productStoreId = ProductStoreID.builder()
                    .productId(product.getId())
                    .storeId(store.getId())
                    .build();

            ProductStore productStore = productStoreRepository.findById(productStoreId)
                    .orElse(ProductStore.builder()
                            .id(productStoreId)
                            .quantityInStock(0)
                            .build());

            productStore.setQuantityInStock(productStore.getQuantityInStock() + item.getQuantity());
            productStoreRepository.save(productStore);

            itemResponses.add(TransferItemResponse.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .batchId(batch.getId())
                    .quantity(item.getQuantity())
                    .build());

            totalQuantity += item.getQuantity();
        }

        log.info("Transfer completed. Total items: {}, Total quantity: {}", itemResponses.size(), totalQuantity);

        return StoreTransferResponse.builder()
                .transferId(exportDetail.getDetailsID())
                .fromWarehouseId(warehouse.getId())
                .fromWarehouseName(warehouse.getName())
                .toStoreId(store.getId())
                .toStoreName(store.getName())
                .transferDate(exportDetail.getExportDate())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .notes(request.getNotes())
                .totalQuantity(totalQuantity)
                .items(itemResponses)
                .build();
    }

    /**
     * Get import history
     */
    @Transactional(readOnly = true)
    public List<WarehouseImportResponse> getImportHistory(Long warehouseId) {
        List<WarehouseImportDetail> imports = warehouseId != null
                ? importDetailRepository.findByWarehouseId(warehouseId)
                : importDetailRepository.findAll();

        return imports.stream().map(this::toImportResponse).toList();
    }

    /**
     * Get export history
     */
    @Transactional(readOnly = true)
    public List<WarehouseExportResponse> getExportHistory(Long warehouseId) {
        List<WarehouseExportDetail> exports = warehouseId != null
                ? exportDetailRepository.findByWarehouseId(warehouseId)
                : exportDetailRepository.findAll();

        return exports.stream().map(this::toExportResponse).toList();
    }

    private WarehouseImportResponse toImportResponse(WarehouseImportDetail detail) {
        Warehouse warehouse = warehouseRepository.findById(detail.getWarehouseID()).orElse(null);
        Employee employee = employeeRepository.findById(detail.getEmployeeImportId()).orElse(null);

        return WarehouseImportResponse.builder()
                .detailsId(detail.getDetailsID())
                .warehouseId(detail.getWarehouseID())
                .warehouseName(warehouse != null ? warehouse.getName() : "Unknown")
                .supplier(detail.getSupplier())
                .unitPrice(BigDecimal.valueOf(detail.getUnitPrice()))
                .importDate(detail.getImportDate())
                .employeeName(employee != null ? employee.getFirstName() + " " + employee.getLastName() : "Unknown")
                .build();
    }

    private WarehouseExportResponse toExportResponse(WarehouseExportDetail detail) {
        Warehouse warehouse = warehouseRepository.findById(detail.getWarehouseID()).orElse(null);
        Employee employee = employeeRepository.findById(detail.getEmployeeExportId()).orElse(null);

        return WarehouseExportResponse.builder()
                .detailsId(detail.getDetailsID())
                .warehouseId(detail.getWarehouseID())
                .warehouseName(warehouse != null ? warehouse.getName() : "Unknown")
                .reason(detail.getReason())
                .exportDate(detail.getExportDate())
                .employeeName(employee != null ? employee.getFirstName() + " " + employee.getLastName() : "Unknown")
                .build();
    }
}
