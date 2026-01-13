package com.empresa.contabil.domain.repository;

import com.empresa.contabil.domain.model.RegraPreenchimento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegraRepository {
    RegraPreenchimento salvar(RegraPreenchimento regra);
    Optional<RegraPreenchimento> buscarPorId(UUID id);
    List<RegraPreenchimento> buscarTodos();
    List<RegraPreenchimento> buscarPorClienteId(UUID clienteId);
    List<RegraPreenchimento> buscarAtivasPorClienteId(UUID clienteId);
    void deletar(UUID id);
    boolean existe(UUID id);
}
