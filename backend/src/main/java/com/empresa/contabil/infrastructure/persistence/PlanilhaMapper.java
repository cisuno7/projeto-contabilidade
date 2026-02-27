package com.empresa.contabil.infrastructure.persistence;

import com.empresa.contabil.domain.model.Planilha;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanilhaMapper {

    private final ObjectMapper objectMapper;

    public PlanilhaEntity toEntity(Planilha planilha) {
        if (planilha == null) {
            return null;
        }

        JsonNode aiMetadata = toJsonNode(planilha.getAiMetadata());

        return PlanilhaEntity.builder()
                .id(planilha.getId())
                .clientId(planilha.getClienteId())
                .originalFilename(planilha.getNomeArquivo())
                .storagePath(planilha.getCaminhoArquivo())
                .status(planilha.getStatus())
                .processingLogs(planilha.getProcessingLogs())
                .aiMetadata(aiMetadata)
                .createdAt(planilha.getDataCriacao())
                .updatedAt(planilha.getDataAtualizacao())
                .build();
    }

    public Planilha toDomain(PlanilhaEntity entity) {
        if (entity == null) {
            return null;
        }

        String aiMetadata = toString(entity.getAiMetadata());

        return Planilha.builder()
                .id(entity.getId())
                .clienteId(entity.getClientId())
                .nomeArquivo(entity.getOriginalFilename())
                .caminhoArquivo(entity.getStoragePath())
                .status(entity.getStatus())
                .dataCriacao(entity.getCreatedAt())
                .dataAtualizacao(entity.getUpdatedAt())
                .processingLogs(entity.getProcessingLogs())
                .aiMetadata(aiMetadata)
                .build();
    }

    private JsonNode toJsonNode(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readTree(value);
        } catch (Exception e) {
            return objectMapper.valueToTree(value);
        }
    }

    private String toString(JsonNode node) {
        if (node == null) {
            return null;
        }
    
        if (node.isTextual()) {
            return node.asText();
        }
    
        return node.toPrettyString();
    }
}
