package com.empresa.contabil.application.usecase;

import com.empresa.contabil.domain.model.Planilha;
import com.empresa.contabil.domain.repository.PlanilhaRepository;
import com.empresa.contabil.infrastructure.filestorage.FileStorageService;
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
    
    @Override
    public byte[] executar(UUID planilhaId) {
        log.info("Iniciando download da planilha: {}", planilhaId);
        
        Planilha planilha = planilhaRepository.buscarPorId(planilhaId)
                .orElseThrow(() -> new RuntimeException("Planilha não encontrada: " + planilhaId));
        
        try {
            InputStream inputStream = fileStorageService.ler(planilha.getCaminhoArquivo());
            byte[] bytes = inputStream.readAllBytes();
            inputStream.close();
            
            log.info("Planilha lida com sucesso: {}", planilhaId);
            return bytes;
            
        } catch (IOException e) {
            log.error("Erro ao ler arquivo da planilha", e);
            throw new RuntimeException("Erro ao ler arquivo da planilha: " + e.getMessage(), e);
        }
    }
}
