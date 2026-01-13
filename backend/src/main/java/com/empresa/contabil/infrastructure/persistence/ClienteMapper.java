package com.empresa.contabil.infrastructure.persistence;

import com.empresa.contabil.domain.model.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {
    
    public ClienteEntity toEntity(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        
        return ClienteEntity.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .cnpj(cliente.getCnpj())
                .email(cliente.getEmail())
                .telefone(cliente.getTelefone())
                .build();
    }
    
    public Cliente toDomain(ClienteEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Cliente.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .cnpj(entity.getCnpj())
                .email(entity.getEmail())
                .telefone(entity.getTelefone())
                .dataCriacao(entity.getDataCriacao())
                .dataAtualizacao(entity.getDataAtualizacao())
                .build();
    }
}
