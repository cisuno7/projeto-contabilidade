package com.empresa.contabil.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username ou email é obrigatório")
    private String usernameOrEmail;

    @NotBlank(message = "Senha é obrigatória")
    private String password;
}