package com.example.supermarket.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "first_name", length = 100, nullable = false)
    String firstName;

    @Column(name = "last_name", length = 100, nullable = false)
    String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    String email;

    @Column(name = "phone", length = 20)
    String phone;

    @Column(name = "address")
    String address;

    @Column(name = "loyalty_points", columnDefinition = "INT DEFAULT 0")
    Integer loyaltyPoints;

    @Column(nullable = false)
    String password;
}