package com.empresa.contabil.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "correcao_ai_metadata")
@Getter
public class CorrecaoAIMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ======================
    // Relacionamento
    // ======================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    // ======================
    // Dados da correção
    // ======================

    @Column(nullable = false)
    private String campo;

    @Column(name = "valor_original", nullable = false, length = 255)
    private String valorOriginal;

    @Column(name = "valor_corrigido", nullable = false, length = 255)
    private String valorCorrigido;

    @Column(nullable = false, length = 500)
    private String motivo;

    // ======================
    // Controle temporal
    // ======================

    @Column(nullable = false)
    private LocalDateTime dataCorrecao;
}
