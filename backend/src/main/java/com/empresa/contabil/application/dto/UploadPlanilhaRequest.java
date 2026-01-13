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
}
