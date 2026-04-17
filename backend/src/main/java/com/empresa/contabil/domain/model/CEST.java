package com.empresa.contabil.domain.model;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Column(name = "uf", nullable = true, length = 2)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String uf;

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