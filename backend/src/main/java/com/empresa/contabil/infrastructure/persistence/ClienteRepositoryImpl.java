package com.empresa.contabil.infrastructure.persistence;

import com.empresa.contabil.domain.model.Cliente;
import com.empresa.contabil.domain.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ClienteRepositoryImpl implements ClienteRepository {
    
    private final ClienteJpaRepository jpaRepository;
    private final ClienteMapper mapper;
    
    @Override
    public Cliente salvar(Cliente cliente) {
        ClienteEntity entity = mapper.toEntity(cliente);
        ClienteEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<Cliente> buscarPorCnpj(String cnpj) {
        return jpaRepository.findByDocumentNumber(cnpj)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Cliente> buscarTodos() {
        return jpaRepository.findAll().stream()
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
    
    @Override
    public boolean existePorCnpj(String cnpj) {
        return jpaRepository.existsByDocumentNumber(cnpj);
    }
}
