package com.example.supermarket.repository;

import com.example.supermarket.entity.WarehouseEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseEmployeeRepository extends JpaRepository<WarehouseEmployee, Long> {
//    List<WarehouseEmployee> findByWarehouseID(Long warehouseId);
}
