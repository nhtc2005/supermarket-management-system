package com.example.supermarket.repository;

import com.example.supermarket.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProductId(Long productId);
    Integer countByProductId(Long productId);

    @Query("SELECT SUM(b.quantityAvailable) FROM Batch b WHERE b.productId = :productId")
    Integer sumTotalStockByProductId(@Param("productId") Long productId);
}
