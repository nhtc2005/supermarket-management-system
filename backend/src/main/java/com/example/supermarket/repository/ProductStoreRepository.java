package com.example.supermarket.repository;

import com.example.supermarket.entity.ProductStore;
import com.example.supermarket.entity.compositePk.ProductStoreID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductStoreRepository extends JpaRepository<ProductStore, ProductStoreID> {
    List<ProductStore> findByIdProductId(Long productId);
//    List<ProductStore> findByIdStoreId(Long storeId);
}
