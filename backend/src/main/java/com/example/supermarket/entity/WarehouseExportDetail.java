package com.example.supermarket.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "warehouse_exports_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class WarehouseExportDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "details_id")
    private long detailsID;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "employee_export")
    private Long employeeExportId;

    @CreationTimestamp
    @Column(name = "export_date", nullable = false, updatable = false)
    private LocalDateTime exportDate;

    @Column(name = "warehouse_id")
    private long warehouseID;
}