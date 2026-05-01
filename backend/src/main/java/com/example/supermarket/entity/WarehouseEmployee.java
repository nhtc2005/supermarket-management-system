package com.example.supermarket.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "warehouse_employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class WarehouseEmployee {
    @Id
    @Column(name = "employee_id")
    Long employeeID;

    @Column(name = "warehouse_id")
    Long warehouseID;
}
