package com.empresa.contabil.domain.repository;

import com.empresa.contabil.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> buscarPorId(UUID id);

    Optional<User> buscarPorEmail(String email);

    Optional<User> buscarPorUsername(String username);

    List<User> buscarTodos();

    User salvar(User user);

    void deletar(UUID id);

    boolean existePorEmail(String email);

    boolean existePorUsername(String username);
}