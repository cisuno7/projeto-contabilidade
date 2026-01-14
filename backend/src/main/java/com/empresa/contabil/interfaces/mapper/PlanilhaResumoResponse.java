package com.empresa.contabil.interfaces.mapper;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PlanilhaResumoResponse {

    private UUID id;
    private String nomeArquivo;
    private String tipoArquivo;
    private String status;
    private UUID clienteId;
    private LocalDateTime dataUpload;
    private LocalDateTime dataProcessamento;
    private boolean podeBaixar;
}


