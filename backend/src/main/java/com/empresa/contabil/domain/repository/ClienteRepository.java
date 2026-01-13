package com.empresa.contabil.domain.repository;

import com.empresa.contabil.domain.model.Cliente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository {
    Cliente salvar(Cliente cliente);
    Optional<Cliente> buscarPorId(UUID id);
    Optional<Cliente> buscarPorCnpj(String cnpj);
    List<Cliente> buscarTodos();
    void deletar(UUID id);
    boolean existe(UUID id);
    boolean existePorCnpj(String cnpj);
}
