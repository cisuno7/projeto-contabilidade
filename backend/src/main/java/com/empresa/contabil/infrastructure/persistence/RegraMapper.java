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
                .nomeCampo(regra.getNomeCampo())
                .descricao(regra.getDescricao())
                .tipoRegra(regra.getTipoRegra())
                .expressao(regra.getExpressao())
                .prioridade(regra.getPrioridade())
                .ativo(regra.getAtivo())
                .clienteId(regra.getClienteId())
                .build();
    }
    
    public RegraPreenchimento toDomain(RegraEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return RegraPreenchimento.builder()
                .id(entity.getId())
                .nomeCampo(entity.getNomeCampo())
                .descricao(entity.getDescricao())
                .tipoRegra(entity.getTipoRegra())
                .expressao(entity.getExpressao())
                .prioridade(entity.getPrioridade())
                .ativo(entity.getAtivo())
                .clienteId(entity.getClienteId())
                .dataCriacao(entity.getDataCriacao())
                .dataAtualizacao(entity.getDataAtualizacao())
                .build();
    }
}
