package com.example.supermarket.entity;

import com.example.supermarket.entity.compositePk.OrderDetailID;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "order_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderDetail {

    @EmbeddedId
    OrderDetailID id;

    Integer quantity;

    @Column(name = "sub_total")
    float subtotal;

    public void setProductId(Long productId) {
        if (this.id == null) {
            this.id = new OrderDetailID();
        }
        this.id.setProductId(productId);
    }

    public Long getProductId() {
        return id != null ? id.getProductId() : null;
    }

    public Long getOrderId() {
        return id != null ? id.getOrderID() : null;
    }
}
