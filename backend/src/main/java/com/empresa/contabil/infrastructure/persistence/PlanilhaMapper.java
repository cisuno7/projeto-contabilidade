package com.empresa.contabil.infrastructure.persistence;

import com.empresa.contabil.domain.model.Planilha;
import org.springframework.stereotype.Component;

@Component
public class PlanilhaMapper {
    
    public PlanilhaEntity toEntity(Planilha planilha) {
        if (planilha == null) {
            return null;
        }
        
        return PlanilhaEntity.builder()
                .id(planilha.getId())
                .clientId(planilha.getClienteId())
                .originalFilename(planilha.getNomeArquivo())
                .storagePath(planilha.getCaminhoArquivo())
                .status(planilha.getStatus())
                .createdAt(planilha.getDataCriacao())
                .updatedAt(planilha.getDataAtualizacao())
                .build();
    }
    
    public Planilha toDomain(PlanilhaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Planilha.builder()
                .id(entity.getId())
                .clienteId(entity.getClientId())
                .nomeArquivo(entity.getOriginalFilename())
                .caminhoArquivo(entity.getStoragePath())
                .status(entity.getStatus())
                .dataCriacao(entity.getCreatedAt())
                .dataAtualizacao(entity.getUpdatedAt())
                .build();
    }
}
