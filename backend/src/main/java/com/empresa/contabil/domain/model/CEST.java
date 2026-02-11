package com.empresa.contabil.domain.model;

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

    @Column(nullable = false, length = 10)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ncm_id", nullable = false)
    private NCM ncm;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private boolean valido;

protected CEST() {}

public CEST(String codigo, NCM ncm, String descricao) {
    this.codigo = codigo;
    this.ncm = ncm;
    this.descricao = descricao;
    this.valido = true;
}


    public void atualizarDescricao(String descricao) {
    this.descricao = descricao;
}


}


