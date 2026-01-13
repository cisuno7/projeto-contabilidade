package com.empresa.contabil.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegraPreenchimento {
    private UUID id;
    private String nomeCampo;
    private String descricao;
    private String tipoRegra; // IA, FORMULA, VALOR_FIXO, REFERENCIA
    private String expressao; // Fórmula ou prompt para IA
    private Integer prioridade;
    private Boolean ativo;
    private UUID clienteId;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    
    public void ativar() {
        this.ativo = true;
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public void desativar() {
        this.ativo = false;
        this.dataAtualizacao = LocalDateTime.now();
    }
}
