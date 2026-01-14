package com.empresa.contabil.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private UUID userId;
    private String username;
    private String email;
    private String role;
    private String token;
    private String tokenType = "Bearer";

    public AuthResponse(UUID userId, String username, String email, String role, String token) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.token = token;
        this.tokenType = "Bearer";
    }
}