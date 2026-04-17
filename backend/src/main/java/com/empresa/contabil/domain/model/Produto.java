package com.empresa.contabil.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import lombok.Getter;

@Getter
@Entity
@Table(name = "produto")

public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "uf", nullable = true, length = 2)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String uf;

    // ======================
    // Dados brutos da planilha
    // ======================

    @Column(nullable = false)
    private String nome;

    private String grupo;

    @Column(length = 4)
    private String csosn;

    // ======================
    // Referências validadas
    // ======================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ncm_id")
    private NCM ncm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cest_id")
    private CEST cest;

    // ======================
    // Controle de validação
    // ======================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusValidacaoProduto statusValidacao;

    private LocalDateTime dataProcessamento;

    // ======================
    // Outros dados úteis
    // ======================
    @Column(precision = 15, scale = 2)
    private BigDecimal valorUnitario;

    public void definirStatus(StatusValidacaoProduto status) {
    this.statusValidacao = status;
}

public void definirNcm(NCM ncm) {
    this.ncm = ncm;
}

public void definirCest(CEST cest) {
    this.cest = cest;
}

public void definirDataProcessamento(LocalDateTime data) {
    this.dataProcessamento = data;
}

    /**
     * Compatibilidade: no banco só existem {@code ncm_id} / {@code cest_id}; o “informado” vem do vínculo quando carregado.
     */
    public String getCodigoNcmInformado() {
        return ncm != null ? ncm.getCodigo() : null;
    }

    public String getCodigoCestInformado() {
        return cest != null ? cest.getCodigo() : null;
    }

}
