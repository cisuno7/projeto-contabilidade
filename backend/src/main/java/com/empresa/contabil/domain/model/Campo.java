package com.empresa.contabil.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campo {
    private UUID id;
    private String nome;
    private String tipo; // TEXTO, NUMERO, DATA, BOOLEANO
    private String valor;
    private BigDecimal valorNumerico;
    private Integer linha;
    private Integer coluna;
    private UUID planilhaId;
    
    public boolean isPreenchido() {
        return valor != null && !valor.trim().isEmpty();
    }
}
