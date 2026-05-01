package com.example.supermarket.repository;

import com.example.supermarket.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Boolean existsBySku(String sku);
    Boolean existsByBarcode(String barcode);

    Page<Product> findByNameContaining(String name, Pageable pageable);
    Page<Product> findBySkuContaining(String sku, Pageable pageable);
    Page<Product> findByBarcodeContaining(String barcode, Pageable pageable);
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}
