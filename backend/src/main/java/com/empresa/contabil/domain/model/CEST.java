package com.empresa.contabil.domain.model;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(
    name = "cest",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"codigo", "ncm_id"})
    }
)
@Getter
public class CEST {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 7)
    private String codigo; // Sempre 7 dígitos sem ponto

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ncm_id", nullable = false)
    private NCM ncm;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(nullable = false)
    private boolean valido;

    protected CEST() {}

    public CEST(String codigo, NCM ncm, String descricao) {
        this.codigo = normalizarCodigo(codigo);
        this.ncm = Objects.requireNonNull(ncm);
        this.descricao = Objects.requireNonNull(descricao);
        this.valido = true;
    }

    // =========================
    // REGRAS DE DOMÍNIO
    // =========================

    public boolean compativelCom(String ncmProduto) {
        if (ncmProduto == null) return false;

        String ncmBase = ncm.getCodigo(); // já normalizado no NCM
        return ncmProduto.startsWith(ncmBase);
    }

    public void atualizarDescricao(String descricao) {
        this.descricao = Objects.requireNonNull(descricao);
    }

    public void invalidar() {
        this.valido = false;
    }

    public void reativar() {
        this.valido = true;
    }

    // =========================
    // NORMALIZAÇÃO
    // =========================

    private String normalizarCodigo(String codigo) {
        if (codigo == null) {
            throw new IllegalArgumentException("Código CEST não pode ser nulo.");
        }

        String somenteNumeros = codigo.replaceAll("\\D", "");

        if (somenteNumeros.length() != 7) {
            throw new IllegalArgumentException("CEST deve conter 7 dígitos.");
        }

        return somenteNumeros;
    }
}