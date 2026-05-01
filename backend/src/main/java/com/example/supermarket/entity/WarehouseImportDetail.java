package com.example.supermarket.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "warehouse_imports_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class WarehouseImportDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "details_id")
    Long detailsID;
    
    @Column(name = "supplier")
    String supplier;
    
    @CreationTimestamp
    @Column(name = "import_date", nullable = false, updatable = false)
    LocalDateTime importDate;
    
    @Column(name = "unit_price", nullable = false)
    Float unitPrice;
    
    @Column(name = "employee_import")
    Long employeeImportId;
    
    @Column(name = "warehouse_id")
    Long warehouseID;
}
