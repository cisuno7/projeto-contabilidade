package com.empresa.contabil.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessarPlanilhaRequest {
    private UUID planilhaId;
    private Boolean usarIA;
    /** Linhas estruturadas da planilha de referência (opcional). */
    private List<Map<String, String>> linhasReferencia;
    /** Nome do arquivo base enviado no upload (opcional). */
    private String nomeArquivoReferencia;
}
