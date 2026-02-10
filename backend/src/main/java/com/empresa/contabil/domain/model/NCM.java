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

    @Column(nullable = false, length = 8, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String descricao;

    private LocalDate dataInicioVigencia;
    private LocalDate dataFimVigencia;

    private String atoLegal;
}

