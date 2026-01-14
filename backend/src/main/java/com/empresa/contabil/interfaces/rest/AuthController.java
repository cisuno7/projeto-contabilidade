package com.empresa.contabil.interfaces.rest;

import com.empresa.contabil.application.dto.AuthResponse;
import com.empresa.contabil.application.dto.LoginRequest;
import com.empresa.contabil.application.dto.RegisterRequest;
import com.empresa.contabil.application.usecase.LoginUseCase;
import com.empresa.contabil.application.usecase.RegisterUserUseCase;
import com.empresa.contabil.domain.model.User;
import com.empresa.contabil.interfaces.mapper.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Tentativa de registro para usuário: {}", request.getUsername());

        try {
            User user = registerUserUseCase.execute(request);
            UserResponse response = UserResponse.fromDomain(user);

            log.info("Usuário registrado com sucesso: {}", user.getUsername());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Erro no registro: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro inesperado no registro", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Tentativa de login para: {}", request.getUsernameOrEmail());

        try {
            AuthResponse response = loginUseCase.execute(request);

            log.info("Login realizado com sucesso para: {}", response.getUsername());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Credenciais inválidas para: {}", request.getUsernameOrEmail());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro inesperado no login", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}