package com.example.supermarket.dto.request.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreRequest {
    @NotBlank(message = "Store name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    private String location;
    private Long managerId;
}
