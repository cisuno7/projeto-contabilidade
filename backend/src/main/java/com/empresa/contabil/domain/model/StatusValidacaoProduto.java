package com.empresa.contabil.domain.model;

public enum StatusValidacaoProduto {
    PENDENTE,
    /** Valor usado na carga/ETL no banco (ex.: Supabase) quando o registro já foi tratado. */
    PROCESSADO,
    VALIDADO,
    INVALIDO
}