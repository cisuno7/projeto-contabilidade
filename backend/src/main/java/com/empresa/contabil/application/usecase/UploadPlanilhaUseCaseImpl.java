package com.empresa.contabil.application.usecase;

import com.empresa.contabil.application.dto.PlanilhaDTO;
import com.empresa.contabil.application.dto.ProcessarPlanilhaRequest;
import com.empresa.contabil.application.dto.UploadPlanilhaRequest;
import com.empresa.contabil.domain.model.Planilha;
import com.empresa.contabil.domain.repository.PlanilhaRepository;
import com.empresa.contabil.infrastructure.filestorage.FileStorageService;
import com.empresa.contabil.interfaces.mapper.PlanilhaDTOMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadPlanilhaUseCaseImpl implements UploadPlanilhaUseCase {
    
    private final FileStorageService fileStorageService;
    private final PlanilhaRepository planilhaRepository;
    private final PlanilhaDTOMapper planilhaDTOMapper;
    private final ProcessarPlanilhaUseCase processarPlanilhaUseCase;
    
    @Override
    public PlanilhaDTO executar(UploadPlanilhaRequest request, MultipartFile arquivo) {
        log.info("Iniciando upload de planilha para cliente: {}", request.getClienteId());
        
        try {
            // Determinar tipo de arquivo
            String tipoArquivo = determinarTipoArquivo(arquivo.getOriginalFilename());
            String nomeArquivo = request.getNomeArquivo() != null 
                    ? request.getNomeArquivo() 
                    : arquivo.getOriginalFilename();
            
            // Salvar arquivo no sistema de arquivos
            String caminhoArquivo = fileStorageService.salvar(arquivo, nomeArquivo);
            
            // Criar entidade de domínio
            Planilha planilha = Planilha.builder()
                    .id(UUID.randomUUID())
                    .nomeArquivo(nomeArquivo)
                    .tipoArquivo(tipoArquivo)
                    .caminhoArquivo(caminhoArquivo)
                    .status(Planilha.StatusPlanilha.RECEBIDA)
                    .clienteId(request.getClienteId())
                    .dataUpload(LocalDateTime.now())
                    .build();
            
            // Salvar no banco de dados
            Planilha planilhaSalva = planilhaRepository.salvar(planilha);
            
            // Se corrigirComIA estiver ativado, disparar processamento imediato
            if (Boolean.TRUE.equals(request.getCorrigirComIA())) {
                log.info("CorrigirComIA ativado - disparando processamento para planilha {}", planilhaSalva.getId());
                try {
                    PlanilhaDTO processada = processarPlanilhaUseCase.executar(
                            ProcessarPlanilhaRequest.builder()
                                    .planilhaId(planilhaSalva.getId())
                                    .usarIA(true)
                                    .ufConferencia(request.getUfConferencia())
                                    .build()
                    );
                    return processada;
                } catch (Exception e) {
                    log.error("Erro ao processar planilha após upload", e);
                    Throwable cause = e.getCause();
                    if (cause instanceof IllegalArgumentException iae) {
                        throw iae;
                    }
                    throw new RuntimeException(cause != null ? cause.getMessage() : e.getMessage(), e);
                }
            }
            
            log.info("Planilha salva com sucesso: {} (corrigirComIA={})", planilhaSalva.getId(), request.getCorrigirComIA());
            return planilhaDTOMapper.toDTO(planilhaSalva);
            
        } catch (Exception e) {
            log.error("Erro ao fazer upload da planilha", e);
            throw new RuntimeException("Erro ao fazer upload da planilha: " + e.getMessage(), e);
        }
    }
    
    private String determinarTipoArquivo(String nomeArquivo) {
        if (nomeArquivo == null) {
            return "XLSX";
        }
        
        String extensao = nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1).toUpperCase();
        return extensao.equals("CSV") ? "CSV" : "XLSX";
    }

}
