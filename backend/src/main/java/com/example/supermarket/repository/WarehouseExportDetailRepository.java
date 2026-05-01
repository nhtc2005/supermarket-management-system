package com.example.supermarket.repository;

import com.example.supermarket.entity.WarehouseExportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WarehouseExportDetailRepository extends JpaRepository<WarehouseExportDetail, Long> {
    @Query("SELECT w FROM WarehouseExportDetail w WHERE w.warehouseID = :warehouseId ORDER BY w.exportDate DESC")
    List<WarehouseExportDetail> findByWarehouseId(@Param("warehouseId") Long warehouseId);

    @Query("SELECT w FROM WarehouseExportDetail w WHERE w.exportDate BETWEEN :fromDate AND :toDate")
    List<WarehouseExportDetail> findByExportDateBetween(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("SELECT w FROM WarehouseExportDetail w WHERE w.reason LIKE %:reason%")
    List<WarehouseExportDetail> findByReasonContaining(@Param("reason") String reason);
}
