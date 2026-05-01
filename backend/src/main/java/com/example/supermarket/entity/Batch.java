package com.example.supermarket.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "batches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "variant_id", nullable = false)
    Long variantId;

    @Column(name = "product_id", nullable = false)
    Long productId;

    @Column(name = "warehouse_id", nullable = false)
    Long warehouseId;

    String manufacture;
    String supplier;

    @Column(name = "quantity_total", nullable = false)
    Integer quantityTotal;

    @Column(name = "quantity_available", nullable = false)
    Integer quantityAvailable;

    @Column(name = "create_date")
    LocalDate createDate;

    @Column(name = "expiry_date")
    LocalDate expiryDate;
}
