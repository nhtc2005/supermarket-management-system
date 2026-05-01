package com.example.supermarket.dto.request.customer;

import com.example.supermarket.dto.ValidationGroups;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdateRequest {
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Email(message = "Email must be valid")
    private String email;

    @Pattern(regexp="^[0-9]{10,20}$", message = "Phone number must be 10-20 digits", groups = ValidationGroups.Optional.class)
    private String phone;

    private String address;
}
