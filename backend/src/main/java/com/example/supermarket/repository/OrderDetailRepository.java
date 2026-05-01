package com.example.supermarket.repository;

import com.example.supermarket.entity.OrderDetail;
import com.example.supermarket.entity.compositePk.OrderDetailID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailID> {
    List<OrderDetail> findByIdOrderID(Long orderId);
    Long countByIdOrderID(Long orderId);
}
