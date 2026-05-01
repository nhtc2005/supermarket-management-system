package com.example.supermarket.util;

import com.example.supermarket.config.JwtAuthenticationFilter;
import com.example.supermarket.exception.UnauthorizedError;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility class for security-related operations
 */
@Component
public class SecurityUtils {

    /**
     * Get currently authenticated user
     */
    public static JwtAuthenticationFilter.AuthenticatedUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedError("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof JwtAuthenticationFilter.AuthenticatedUser) {
            return (JwtAuthenticationFilter.AuthenticatedUser) principal;
        }

        throw new UnauthorizedError("Invalid authentication principal");
    }

    /**
     * Get current user ID
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    /**
     * Get current user identifier (email for customer, username for employee)
     */
    public static String getCurrentUserIdentifier() {
        return getCurrentUser().getIdentifier();
    }

    /**
     * Get current user type (CUSTOMER or EMPLOYEE)
     */
    public static String getCurrentUserType() {
        return getCurrentUser().getUserType();
    }

    /**
     * Check if current user is a customer
     */
    public static boolean isCurrentUserCustomer() {
        return getCurrentUser().isCustomer();
    }

    /**
     * Check if current user is an employee
     */
    public static boolean isCurrentUserEmployee() {
        return getCurrentUser().isEmployee();
    }

    /**
     * Check if user has specific role
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }

    /**
     * Verify that the requested resource belongs to the current user
     * Useful for endpoints like /customers/me
     */
    public static void verifyResourceOwnership(Long resourceUserId) {
        Long currentUserId = getCurrentUserId();
        if (!currentUserId.equals(resourceUserId)) {
            throw new UnauthorizedError("You don't have permission to access this resource");
        }
    }
}