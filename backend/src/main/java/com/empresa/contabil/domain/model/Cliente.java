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
public class Cliente {
    private UUID id;
    private String nome;
    private String cnpj;
    private String email;
    private String telefone;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    
    public void atualizar(String nome, String email, String telefone) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.dataAtualizacao = LocalDateTime.now();
    }
}
