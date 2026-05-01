package com.example.supermarket.entity;

import com.example.supermarket.entity.compositePk.ProductStoreID;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "product_stores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProductStore {
    @EmbeddedId
    ProductStoreID id;

    @Column(name = "quantity_in_stock")
    Integer quantityInStock;
}
