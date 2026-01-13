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
                .nomeArquivo(planilha.getNomeArquivo())
                .tipoArquivo(planilha.getTipoArquivo())
                .caminhoArquivo(planilha.getCaminhoArquivo())
                .status(planilha.getStatus())
                .clienteId(planilha.getClienteId())
                .dataUpload(planilha.getDataUpload())
                .dataProcessamento(planilha.getDataProcessamento())
                .build();
    }
    
    public Planilha toDomain(PlanilhaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Planilha.builder()
                .id(entity.getId())
                .nomeArquivo(entity.getNomeArquivo())
                .tipoArquivo(entity.getTipoArquivo())
                .caminhoArquivo(entity.getCaminhoArquivo())
                .status(entity.getStatus())
                .clienteId(entity.getClienteId())
                .dataUpload(entity.getDataUpload())
                .dataProcessamento(entity.getDataProcessamento())
                .dataCriacao(entity.getDataCriacao())
                .dataAtualizacao(entity.getDataAtualizacao())
                .build();
    }
}
