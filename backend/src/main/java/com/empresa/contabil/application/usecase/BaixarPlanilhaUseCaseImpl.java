package com.empresa.contabil.application.usecase;

import com.empresa.contabil.domain.model.Planilha;
import com.empresa.contabil.domain.repository.PlanilhaRepository;
import com.empresa.contabil.infrastructure.filestorage.FileStorageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaixarPlanilhaUseCaseImpl implements BaixarPlanilhaUseCase {
    
    private final PlanilhaRepository planilhaRepository;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public byte[] executar(UUID planilhaId) {
        log.info("Iniciando download da planilha: {}", planilhaId);
        
        Planilha planilha = planilhaRepository.buscarPorId(planilhaId)
                .orElseThrow(() -> new RuntimeException("Planilha não encontrada: " + planilhaId));
        
        String caminhoArquivo = obterCaminhoArquivo(planilha);
        
        try {
            InputStream inputStream = fileStorageService.ler(caminhoArquivo);
            byte[] bytes = inputStream.readAllBytes();
            inputStream.close();
            
            log.info("Planilha lida com sucesso: {} (arquivo: {})", planilhaId, caminhoArquivo);
            return bytes;
            
        } catch (IOException e) {
            log.error("Erro ao ler arquivo da planilha", e);
            throw new RuntimeException("Erro ao ler arquivo da planilha: " + e.getMessage(), e);
        }
    }
    
    private String obterCaminhoArquivo(Planilha planilha) {
        String aiMetadata = planilha.getAiMetadata();
        if (aiMetadata != null && !aiMetadata.isBlank()) {
            try {
                JsonNode root = objectMapper.readTree(aiMetadata);
                JsonNode pathNode = root.get("processedFilePath");
                if (pathNode != null && pathNode.isTextual()) {
                    String path = pathNode.asText();
                    if (fileStorageService.existe(path)) {
                        log.info("Usando planilha corrigida para download: {}", path);
                        return path;
                    }
                }
            } catch (Exception e) {
                log.debug("ai_metadata não contém processedFilePath ou errou ao parsear: {}", e.getMessage());
            }
        }
        return planilha.getCaminhoArquivo();
    }
}
