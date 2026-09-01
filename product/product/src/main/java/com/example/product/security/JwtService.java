package com.example.product.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long accessExpiration;
    public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.access-expiration}") long accessExpiration) {
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.accessExpiration = accessExpiration;
    }
    public String accessToken(Account account) { return Jwts.builder().subject(account.getUsername()).claim("role", account.getRole()).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + accessExpiration)).signWith(key).compact(); }
    public String username(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject(); }
    public String role(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("role", String.class); }
}