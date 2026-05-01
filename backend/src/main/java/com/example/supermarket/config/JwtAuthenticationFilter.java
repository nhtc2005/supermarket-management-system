package com.example.supermarket.config;

import com.example.supermarket.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Skip authentication for auth endpoints
        String requestPath = request.getRequestURI();
        if (requestPath.startsWith("/api/v1/auth/login")
                || requestPath.startsWith("/api/v1/auth/register")
                || requestPath.startsWith("/api/v1/auth/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get Authorization header
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract JWT token
            final String jwt = authHeader.substring(7);

            // Extract information from token
            final String subject = jwtUtil.extractSubject(jwt);
            final String userType = jwtUtil.extractUserType(jwt);
            final Long userId = jwtUtil.extractUserId(jwt);

            // If subject is valid and no authentication exists
            if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Validate token
                if (jwtUtil.validateToken(jwt, subject)) {

                    // Create authority based on user type
                    List<SimpleGrantedAuthority> authorities = createAuthorities(userType, jwt);

                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            new AuthenticatedUser(userId, subject, userType),
                            null,
                            authorities
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("Successfully authenticated user: {} (Type: {})", subject, userType);
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Create authorities based on user type
     */
    private List<SimpleGrantedAuthority> createAuthorities(String userType, String jwt) {
        if ("CUSTOMER".equals(userType)) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        } else if ("EMPLOYEE".equals(userType)) {
            String employeeType = jwtUtil.extractEmployeeType(jwt);
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + employeeType));
        }
        return Collections.emptyList();
    }

    /**
     * Inner class to hold authenticated user information
     */
    public static class AuthenticatedUser {
        private final Long userId;
        private final String identifier;
        private final String userType;

        public AuthenticatedUser(Long userId, String identifier, String userType) {
            this.userId = userId;
            this.identifier = identifier;
            this.userType = userType;
        }

        public Long getUserId() {
            return userId;
        }

        public String getIdentifier() {
            return identifier;
        }

        public String getUserType() {
            return userType;
        }

        public boolean isCustomer() {
            return "CUSTOMER".equals(userType);
        }

        public boolean isEmployee() {
            return "EMPLOYEE".equals(userType);
        }
    }

    /**
     * Kiểm tra endpoint có phải là public không
     */
    private boolean isPublicEndpoint(HttpServletRequest request) {
        String originalPath = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = originalPath;

        log.info("Original URI: {}, Context Path: {}", originalPath, contextPath);

        // Remove context path from URI (/api/v1)
        if (contextPath != null && !contextPath.isEmpty()) {
            path = path.substring(contextPath.length());
        }

        log.info("Final path for check: {}", path);

        // Public endpoints - không cần authentication
        boolean isPublic = path.equals("/auth/login") ||
                path.equals("/auth/register") ||
                path.equals("/auth/refresh") ||
                path.startsWith("/public/") ||
                path.equals("/swagger-ui.html") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/api-docs") ||
                path.startsWith("/v3/api-docs") ||
                path.equals("/favicon.ico") ||
                path.startsWith("/webjars/") ||
                path.startsWith("/actuator/");

        log.info("Is public endpoint: {}", isPublic);
        return isPublic;
    }
}