package com.empresa.contabil.infrastructure.persistence;

import com.empresa.contabil.domain.model.RegraPreenchimento;
import com.empresa.contabil.domain.repository.RegraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RegraRepositoryImpl implements RegraRepository {
    
    private final RegraJpaRepository jpaRepository;
    private final RegraMapper mapper;
    
    @Override
    public RegraPreenchimento salvar(RegraPreenchimento regra) {
        RegraEntity entity = mapper.toEntity(regra);
        RegraEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<RegraPreenchimento> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<RegraPreenchimento> buscarTodos() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<RegraPreenchimento> buscarPorClienteId(UUID clienteId) {
        return jpaRepository.findByClienteId(clienteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<RegraPreenchimento> buscarAtivasPorClienteId(UUID clienteId) {
        return jpaRepository.findByClienteIdAndAtivoTrue(clienteId).stream()
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
