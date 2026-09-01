package com.example.product.security;

import jakarta.persistence.*;

@Entity
@Table(name = "account", indexes = @Index(name = "idx_account_username", columnList = "username", unique = true))
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 100) private String username;
    @Column(nullable = false) private String password;
    @Column(nullable = false, length = 30) private String role;
    protected Account() { }
    public Account(String username, String password, String role) { this.username = username; this.password = password; this.role = role; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}