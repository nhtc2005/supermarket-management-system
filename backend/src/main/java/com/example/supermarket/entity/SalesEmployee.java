package com.example.supermarket.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "sales_employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class SalesEmployee {
    @Id
    @Column(name = "employee_id")
    Long employeeID;

    @Column(name = "store_id")
    Long storeID;

    @Column(name = "total_sales")
    Double totalSales;
}
