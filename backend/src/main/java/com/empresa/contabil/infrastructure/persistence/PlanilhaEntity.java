package com.empresa.contabil.infrastructure.persistence;

import com.empresa.contabil.domain.model.Planilha;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "planilhas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanilhaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(nullable = false)
    private String nomeArquivo;
    
    @Column(nullable = false)
    private String tipoArquivo;
    
    @Column(nullable = false)
    private String caminhoArquivo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Planilha.StatusPlanilha status;
    
    @Column(nullable = false)
    private UUID clienteId;
    
    private LocalDateTime dataUpload;
    private LocalDateTime dataProcessamento;
    
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
