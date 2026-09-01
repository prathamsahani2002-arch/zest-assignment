package com.example.product.security;

import io.jsonwebtoken.JwtException;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.*;
import java.util.List;

@Configuration @EnableMethodSecurity
public class SecurityConfig {
    @Value("${app.cors.origin:http://localhost:3000}") private String corsOrigin;
    @Value("${app.security.require-https:false}") private boolean requireHttps;
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean UserDetailsService userDetailsService(AccountRepository accounts) {
        return username -> accounts.findByUsername(username)
            .map(account -> User.withUsername(account.getUsername()).password(account.getPassword()).roles(account.getRole()).build())
            .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }
    @Bean SecurityFilterChain filterChain(HttpSecurity http, JwtService jwt) throws Exception {
        http.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(corsConfigurationSource())).sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/auth/**", "/swagger-ui/**", "/swagger-ui.html", "/api-docs/**").permitAll().requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasRole("ADMIN").anyRequest().authenticated())
            .addFilterBefore(new JwtFilter(jwt), UsernamePasswordAuthenticationFilter.class).headers(headers -> headers.httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000)));
        if (requireHttps) http.requiresChannel(channel -> channel.anyRequest().requiresSecure());
        return http.build();
    }
    @Bean CorsConfigurationSource corsConfigurationSource() { CorsConfiguration config = new CorsConfiguration(); config.setAllowedOrigins(List.of(corsOrigin)); config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); config.setAllowedHeaders(List.of("Authorization", "Content-Type")); UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); source.registerCorsConfiguration("/**", config); return source; }
    static class JwtFilter extends OncePerRequestFilter {
        private final JwtService jwt; JwtFilter(JwtService jwt) { this.jwt = jwt; }
        protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain chain) throws java.io.IOException, jakarta.servlet.ServletException { String header = request.getHeader("Authorization"); if (header != null && header.startsWith("Bearer ")) { try { String token = header.substring(7); String username = jwt.username(token); String role = jwt.role(token); var auth = new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))); SecurityContextHolder.getContext().setAuthentication(auth); } catch (JwtException | IllegalArgumentException ignored) { } } chain.doFilter(request, response); }
    }
}