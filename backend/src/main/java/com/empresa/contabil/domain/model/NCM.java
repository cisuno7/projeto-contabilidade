package com.empresa.contabil.domain.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;


@Entity
@Table(name = "ncm")
@Getter
public class NCM {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 8)
    private String codigo;

    @Column(nullable = false)
    private String descricao;

    private LocalDate dataInicioVigencia;
    private LocalDate dataFimVigencia;

    private String atoLegalInicio;

    private Integer numero;

    private Integer ano;

    protected NCM() {
    // construtor protegido para JPA
}
public static NCM criar() {
    return new NCM();
}

    public NCM(String codigo,
               String descricao,
               LocalDate dataInicioVigencia,
               LocalDate dataFimVigencia,
               String atoLegalInicio,
               Integer numero,
               Integer ano) {

        this.codigo = codigo;
        this.descricao = descricao;
        this.dataInicioVigencia = dataInicioVigencia;
        this.dataFimVigencia = dataFimVigencia;
        this.atoLegalInicio = atoLegalInicio;
        this.numero = numero;
        this.ano = ano;
    }

    public void atualizarDescricao(String descricao) {
    this.descricao = descricao;
}

public void atualizarVigencia(LocalDate inicio, LocalDate fim) {
    this.dataInicioVigencia = inicio;
    this.dataFimVigencia = fim;
}

public void atualizarAtoLegal(String atoLegalInicio, Integer numero, Integer ano) {
    this.atoLegalInicio = atoLegalInicio;
    this.numero = numero;
    this.ano = ano;
}

}


