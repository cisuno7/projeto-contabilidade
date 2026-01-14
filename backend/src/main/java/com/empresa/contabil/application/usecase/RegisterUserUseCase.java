package com.empresa.contabil.application.usecase;

import com.empresa.contabil.application.dto.RegisterRequest;
import com.empresa.contabil.domain.model.User;
import com.empresa.contabil.domain.repository.UserRepository;
import com.empresa.contabil.domain.service.PasswordEncoderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;

    public User execute(RegisterRequest request) {
        // Verificar se username já existe
        if (userRepository.existePorUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username já está em uso");
        }

        // Verificar se email já existe
        if (userRepository.existePorEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        // Criar novo usuário
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoderService.encode(request.getPassword()))
                .role(User.UserRole.OPERATOR) // Por padrão, novos usuários são OPERATOR
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return userRepository.salvar(user);
    }
}