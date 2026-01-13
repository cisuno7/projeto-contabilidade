package com.empresa.contabil.domain.repository;

import com.empresa.contabil.domain.model.Planilha;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanilhaRepository {
    Planilha salvar(Planilha planilha);
    Optional<Planilha> buscarPorId(UUID id);
    List<Planilha> buscarTodos();
    List<Planilha> buscarPorClienteId(UUID clienteId);
    List<Planilha> buscarPorStatus(Planilha.StatusPlanilha status);
    void deletar(UUID id);
    boolean existe(UUID id);
}
