package com.example.supermarket.repository;

import com.example.supermarket.entity.WarehouseImportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WarehouseImportDetailRepository extends JpaRepository<WarehouseImportDetail, Long> {
    @Query("SELECT w FROM WarehouseImportDetail w WHERE w.warehouseID = :warehouseId ORDER BY w.importDate DESC")
    List<WarehouseImportDetail> findByWarehouseId(@Param("warehouseId") Long warehouseId);

    @Query("SELECT w FROM WarehouseImportDetail w WHERE w.importDate BETWEEN :fromDate AND :toDate")
    List<WarehouseImportDetail> findByImportDateBetween(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("SELECT w FROM WarehouseImportDetail w WHERE w.supplier LIKE %:supplier%")
    List<WarehouseImportDetail> findBySupplierContaining(@Param("supplier") String supplier);
}
