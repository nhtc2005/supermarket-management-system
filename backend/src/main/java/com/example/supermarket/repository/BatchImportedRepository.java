package com.example.supermarket.repository;

import com.example.supermarket.entity.BatchImported;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BatchImportedRepository extends JpaRepository<BatchImported, Long> {
    @Query("SELECT b FROM BatchImported b WHERE b.importDetailsId = :importDetailsId")
    List<BatchImported> findByImportDetailsId(@Param("importDetailsId") Long importDetailsId);

    @Query("SELECT b FROM BatchImported b WHERE b.batchImportId = :batchId")
    List<BatchImported> findByBatchId(@Param("batchId") Long batchId);
}
