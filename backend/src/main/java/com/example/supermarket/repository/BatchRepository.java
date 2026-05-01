package com.example.supermarket.repository;

import com.example.supermarket.entity.Batch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    List<Batch> findByWarehouseId(Long warehouseId);

    Page<Batch> findByExpiryDateBefore(LocalDate date, Pageable pageable);
}
