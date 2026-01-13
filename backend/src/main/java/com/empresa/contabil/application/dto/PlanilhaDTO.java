package com.empresa.contabil.application.dto;

import com.empresa.contabil.domain.model.Planilha;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanilhaDTO {
    private UUID id;
    private String nomeArquivo;
    private String tipoArquivo;
    private Planilha.StatusPlanilha status;
    private UUID clienteId;
    private LocalDateTime dataUpload;
    private LocalDateTime dataProcessamento;
}
