package com.example.supermarket.repository;

import com.example.supermarket.entity.SalesEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesEmployeeRepository extends JpaRepository<SalesEmployee, Long> {
    List<SalesEmployee> findByStoreID(Long storeId);
}
