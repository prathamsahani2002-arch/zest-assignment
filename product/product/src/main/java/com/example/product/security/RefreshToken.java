package com.example.product.security;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "refresh_token", indexes = @Index(name = "idx_refresh_token_value", columnList = "token_value", unique = true))
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "token_value", nullable = false, unique = true, length = 128) private String tokenValue;
    @Column(nullable = false, length = 100) private String username;
    @Column(nullable = false) private Instant expiresAt;
    @Column(nullable = false) private boolean revoked;
    protected RefreshToken() { }
    public RefreshToken(String tokenValue, String username, Instant expiresAt) { this.tokenValue = tokenValue; this.username = username; this.expiresAt = expiresAt; }
    public String getTokenValue() { return tokenValue; }
    public String getUsername() { return username; }
    public boolean isValid() { return !revoked && expiresAt.isAfter(Instant.now()); }
    public void revoke() { revoked = true; }
}