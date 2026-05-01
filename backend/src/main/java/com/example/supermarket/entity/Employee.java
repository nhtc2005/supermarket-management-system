package com.example.supermarket.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @Column(name = "username", nullable = false, unique = true, length = 255)
    String username;
    
    @Column(name = "password", nullable = false, length = 255)
    String password;
    
    @Column(name = "first_name", length = 100)
    String firstName;
    
    @Column(name = "last_name", length = 100)
    String lastName;

    @Column(name = "hired_at", updatable = false)
    LocalDateTime hiredAt;
    
    @Column(name = "manager_id")
    Long managerID;
}
