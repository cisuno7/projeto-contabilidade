package com.empresa.contabil.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "regras_preenchimento")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegraEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(nullable = false)
    private String nomeCampo;
    
    private String descricao;
    
    @Column(nullable = false)
    private String tipoRegra;
    
    private String expressao;
    
    private Integer prioridade;
    
    @Column(nullable = false)
    private Boolean ativo;
    
    @Column(nullable = false)
    private UUID clienteId;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    
    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;
    
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
