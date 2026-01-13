package com.empresa.contabil.infrastructure.persistence;

import com.empresa.contabil.domain.model.Planilha;
import com.empresa.contabil.domain.repository.PlanilhaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PlanilhaRepositoryImpl implements PlanilhaRepository {
    
    private final PlanilhaJpaRepository jpaRepository;
    private final PlanilhaMapper mapper;
    
    @Override
    public Planilha salvar(Planilha planilha) {
        PlanilhaEntity entity = mapper.toEntity(planilha);
        PlanilhaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<Planilha> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Planilha> buscarTodos() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Planilha> buscarPorClienteId(UUID clienteId) {
        return jpaRepository.findByClienteId(clienteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Planilha> buscarPorStatus(Planilha.StatusPlanilha status) {
        return jpaRepository.findByStatus(status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deletar(UUID id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public boolean existe(UUID id) {
        return jpaRepository.existsById(id);
    }
}
