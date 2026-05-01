package com.example.supermarket.controller.auth;

import com.example.supermarket.config.JwtAuthenticationFilter;
import com.example.supermarket.dto.ApiResponse;
import com.example.supermarket.dto.request.auth.ChangePasswordRequest;
import com.example.supermarket.dto.request.auth.LoginRequest;
import com.example.supermarket.dto.request.auth.RefreshTokenRequest;
import com.example.supermarket.dto.request.customer.CustomerRegisterRequest;
import com.example.supermarket.dto.response.auth.AuthResponse;
import com.example.supermarket.service.AuthServiceI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication APIs for customers and employees")
public class AuthController {
    private final AuthServiceI authService;

    /**
     * Customer registration
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register new customer account",
            description = "Create a new customer account with email and password"
    )
    public ResponseEntity<ApiResponse<AuthResponse>> registerCustomer(
            @Valid @RequestBody CustomerRegisterRequest request
            ) {
        AuthResponse authResponse = authService.registerCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Customer registered successfully", authResponse));
    }

    /**
     * Universal login endpoint
     */
    @PostMapping("/login")
    @Operation(
            summary = "Login to system",
            description = "Login with email (for customers) or username (for employees)"
    )
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
            ) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login Successful", authResponse));
    }

    /**
     * Refresh access token
     */
    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Get a new access token using refresh token"
    )
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
            ) {
        AuthResponse authResponse = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Refresh Token Successful", authResponse));
    }

    /**
     * Get current user information
     */
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get current user information",
            description = "Get profile information of the currently authenticated user"
    )
    public ResponseEntity<ApiResponse<Object>> getCurrentUser(
            @RequestHeader("Authorization") String authorization
    ) {
        String token = authorization.substring(7);
        Object userInfo = authService.getCurrentUser(token);

        return ResponseEntity.ok(ApiResponse.success("User information retrieved successful", userInfo));
    }

    /**
     * Change password
     */
    @PutMapping("/change-password")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Change password",
            description = "Change password for the currently authenticated user"
    )
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthenticationFilter.AuthenticatedUser authenticatedUser =
                (JwtAuthenticationFilter.AuthenticatedUser) authentication.getPrincipal();

        if (authenticatedUser.isCustomer()) {
            authService.changeCustomerPassword(authenticatedUser.getUserId(), request);
        } else {
            authService.changeEmployeePassword(authenticatedUser.getUserId(), request);
        }

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Password changed successfully")
                        .build()
        );
    }
}
