package com.empresa.contabil.infrastructure.persistence;

import com.empresa.contabil.domain.model.RegraPreenchimento;
import org.springframework.stereotype.Component;

@Component
public class RegraMapper {
    
    public RegraEntity toEntity(RegraPreenchimento regra) {
        if (regra == null) {
            return null;
        }
        
        return RegraEntity.builder()
                .id(regra.getId())
                .clientId(regra.getClienteId())
                .description(regra.getDescricao())
                .ruleDefinition(regra.getTipoRegra()) // Ajustar se necessário
                .isActive(regra.getAtivo())
                .createdAt(regra.getDataCriacao())
                .build();
    }
    
    public RegraPreenchimento toDomain(RegraEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return RegraPreenchimento.builder()
                .id(entity.getId())
                .clienteId(entity.getClientId())
                .descricao(entity.getDescription())
                .tipoRegra(entity.getRuleDefinition()) // Ajustar se necessário
                .ativo(entity.getIsActive())
                .dataCriacao(entity.getCreatedAt())
                .build();
    }
}
