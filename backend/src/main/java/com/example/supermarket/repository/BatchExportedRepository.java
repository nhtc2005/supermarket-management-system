package com.example.supermarket.repository;

import com.example.supermarket.entity.BatchExported;
import com.example.supermarket.entity.compositePk.BatchExportedId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BatchExportedRepository extends JpaRepository<BatchExported, BatchExportedId> {
    @Query("SELECT b FROM BatchExported b WHERE b.exportDetailsId = :exportDetailsId")
    List<BatchExported> findByExportDetailsId(@Param("exportDetailsId") Long exportDetailsId);

    @Query("SELECT b FROM BatchExported b WHERE b.batchExportId = :batchId")
    List<BatchExported> findByBatchId(@Param("batchId") Long batchId);
}
