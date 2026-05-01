package com.example.supermarket.repository;

import com.example.supermarket.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
    Page<Order> findByStatus(String status, Pageable pageable);
    Page<Order> findByCustomerIdAndStatus(Long customerId, String status, Pageable pageable);
    Page<Order> findByCreatedAtBetween(LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    Long countByCustomerId(Long customerId);
    Long countByCustomerIdAndStatus(Long customerId, String status);
    Long countByCreatedAtBetween(LocalDateTime fromDate, LocalDateTime toDate);
    Long countByStatusAndCreatedAtBetween(String status, LocalDateTime fromDate, LocalDateTime toDate);

    @Query("SELECT SUM(o.totalMoney) FROM Order o WHERE o.customerId = :customerId")
    Double sumTotalMoneyByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT SUM(o.totalMoney) FROM Order o WHERE o.createdAt BETWEEN :fromDate AND :toDate")
    Double sumTotalMoneyByCreatedAtBetween(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
