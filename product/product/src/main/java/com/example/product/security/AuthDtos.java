package com.example.product.security;
import jakarta.validation.constraints.*;
public final class AuthDtos {
    private AuthDtos() { }
    public record Credentials(@NotBlank @Size(max = 100) String username, @NotBlank @Size(min = 8, max = 200) String password) { }
    public record RefreshRequest(@NotBlank String refreshToken) { }
    public record TokenResponse(String accessToken, String refreshToken) { }
}