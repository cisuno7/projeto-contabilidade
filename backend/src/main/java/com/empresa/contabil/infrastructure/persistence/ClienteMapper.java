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
                .name(cliente.getName())
                .documentNumber(cliente.getDocumentNumber())
                .active(cliente.getActive())
                .createdAt(cliente.getCreatedAt())
                .updatedAt(cliente.getUpdatedAt())
                .build();
    }

    public Cliente toDomain(ClienteEntity entity) {
        if (entity == null) {
            return null;
        }

        return Cliente.builder()
                .id(entity.getId())
                .name(entity.getName())
                .documentNumber(entity.getDocumentNumber())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
