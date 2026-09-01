package com.example.product.security;

import com.example.product.security.AuthDtos.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class AuthService {
    private final AccountRepository accounts; private final RefreshTokenRepository refreshTokens; private final PasswordEncoder encoder; private final JwtService jwt;
    private final long refreshExpiration;
    public AuthService(AccountRepository accounts, RefreshTokenRepository refreshTokens, PasswordEncoder encoder, JwtService jwt, @Value("${app.jwt.refresh-expiration}") long refreshExpiration) { this.accounts = accounts; this.refreshTokens = refreshTokens; this.encoder = encoder; this.jwt = jwt; this.refreshExpiration = refreshExpiration; }
    @Transactional public void register(Credentials credentials) { if (accounts.findByUsername(credentials.username()).isPresent()) throw new IllegalArgumentException("Username is already registered"); accounts.save(new Account(credentials.username(), encoder.encode(credentials.password()), "USER")); }
    @Transactional public TokenResponse login(Credentials credentials) { Account account = accounts.findByUsername(credentials.username()).filter(a -> encoder.matches(credentials.password(), a.getPassword())).orElseThrow(() -> new IllegalArgumentException("Invalid credentials")); return issue(account); }
    @Transactional public TokenResponse refresh(RefreshRequest request) { RefreshToken old = refreshTokens.findByTokenValue(request.refreshToken()).filter(RefreshToken::isValid).orElseThrow(() -> new IllegalArgumentException("Invalid refresh token")); old.revoke(); return issue(accounts.findByUsername(old.getUsername()).orElseThrow(() -> new IllegalArgumentException("Account not found"))); }
    private TokenResponse issue(Account account) { String value = Base64.getUrlEncoder().withoutPadding().encodeToString(new SecureRandom().generateSeed(48)); refreshTokens.save(new RefreshToken(value, account.getUsername(), Instant.now().plusMillis(refreshExpiration))); return new TokenResponse(jwt.accessToken(account), value); }
}