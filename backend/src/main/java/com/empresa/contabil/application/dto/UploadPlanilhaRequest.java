package com.empresa.contabil.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadPlanilhaRequest {
    private UUID clienteId;
    private String nomeArquivo;
    /**
     * Indica se a planilha deverá ser processada com o pipeline de correção por IA
     * (focado em NCM/CEST para SP + Simples Nacional).
     */
    private Boolean corrigirComIA;

    /** UF da base fiscal no banco (ex.: ES). Usado quando {@code corrigirComIA} é true. */
    private String ufConferencia;
}
