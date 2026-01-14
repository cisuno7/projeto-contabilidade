package com.empresa.contabil.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String username;
    private String email;
    private String password; // Será hashada
    private UserRole role;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum UserRole {
        ADMIN,
        OPERATOR
    }

    public void atualizar(String username, String email) {
        this.username = username;
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public void desativar() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void ativar() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }
}