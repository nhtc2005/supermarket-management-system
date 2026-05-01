package com.example.supermarket.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    /**
     * Extract username/identifier from token
     */
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract user type from token (CUSTOMER or EMPLOYEE)
     */
    public String extractUserType(String token) {
        return extractClaim(token, claims -> claims.get("userType", String.class));
    }

    /**
     * Extract user ID from token
     */
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    /**
     * Extract employee type from token (SALES, WAREHOUSE, MANAGER)
     */
    public String extractEmployeeType(String token) {
        return extractClaim(token, claims -> claims.get("employeeType", String.class));
    }

    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract a specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)         // parse token có chữ ký
                .getPayload();
    }


    /**
     * Check if token is expired
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate token
     */
    public Boolean validateToken(String token, String subject) {
        final String tokenSubject = extractSubject(token);
        return (tokenSubject.equals(subject) && !isTokenExpired(token));
    }

    /**
     * Generate access token for customer
     */
    public String generateCustomerAccessToken(Long customerId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", customerId);
        claims.put("userType", "CUSTOMER");
        return createToken(claims, email, expiration);
    }

    /**
     * Generate access token for employee
     */
    public String generateEmployeeAccessToken(Long employeeId, String username, String employeeType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", employeeId);
        claims.put("userType", "EMPLOYEE");
        claims.put("employeeType", employeeType);
        return createToken(claims, username, expiration);
    }

    /**
     * Generate refresh token (generic for both types)
     */
    public String generateRefreshToken(String subject, String userType, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("userType", userType);
        claims.put("tokenType", "REFRESH");
        return createToken(claims, subject, refreshExpiration);
    }

    /**
     * Create token with claims and expiration
     */
    private String createToken(Map<String, Object> claims, String subject, Long validity) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + validity))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Get signing key
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Get expiration time in seconds
     */
    public Long getExpirationTime() {
        return expiration / 1000;
    }

    /**
     * Get refresh expiration time in seconds
     */
    public Long getRefreshExpirationTime() {
        return refreshExpiration / 1000;
    }
}