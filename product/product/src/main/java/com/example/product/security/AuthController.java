package com.example.product.security;
import com.example.product.security.AuthDtos.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService service;
    public AuthController(AuthService service) { this.service = service; }
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED) public void register(@Valid @RequestBody Credentials request) { service.register(request); }
    @PostMapping("/login") public TokenResponse login(@Valid @RequestBody Credentials request) { return service.login(request); }
    @PostMapping("/refresh") public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) { return service.refresh(request); }
}