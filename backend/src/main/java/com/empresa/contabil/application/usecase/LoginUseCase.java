package com.empresa.contabil.application.usecase;

import com.empresa.contabil.application.dto.AuthResponse;
import com.empresa.contabil.application.dto.LoginRequest;
import com.empresa.contabil.domain.model.User;
import com.empresa.contabil.domain.repository.UserRepository;
import com.empresa.contabil.domain.service.JwtService;
import com.empresa.contabil.domain.service.PasswordEncoderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final JwtService jwtService;

    public AuthResponse execute(LoginRequest request) {
        // Tentar buscar por username primeiro, depois por email
        User user = userRepository.buscarPorUsername(request.getUsernameOrEmail())
                .orElseGet(() -> userRepository.buscarPorEmail(request.getUsernameOrEmail())
                        .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas")));

        // Verificar se usuário está ativo
        if (!user.getActive()) {
            throw new IllegalArgumentException("Usuário inativo");
        }

        // Verificar senha
        if (!passwordEncoderService.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }

        // Gerar token JWT
        String token = jwtService.generateToken(user);

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().toString(),
                token
        );
    }
}