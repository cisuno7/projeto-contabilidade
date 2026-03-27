package com.empresa.contabil.application.usecase;

import com.empresa.contabil.application.dto.PlanilhaDTO;
import com.empresa.contabil.application.dto.ProcessarPlanilhaRequest;
import com.empresa.contabil.application.dto.UploadPlanilhaRequest;
import com.empresa.contabil.domain.model.Planilha;
import com.empresa.contabil.domain.repository.PlanilhaRepository;
import com.empresa.contabil.domain.service.InterpretadorPlanilhaService;
import com.empresa.contabil.infrastructure.filestorage.FileStorageService;
import com.empresa.contabil.interfaces.mapper.PlanilhaDTOMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadPlanilhaUseCaseImpl implements UploadPlanilhaUseCase {
    
    private final FileStorageService fileStorageService;
    private final PlanilhaRepository planilhaRepository;
    private final PlanilhaDTOMapper planilhaDTOMapper;
    private final ProcessarPlanilhaUseCase processarPlanilhaUseCase;
    private final InterpretadorPlanilhaService interpretadorPlanilhaService;
    
    @Override
    public PlanilhaDTO executar(UploadPlanilhaRequest request, MultipartFile arquivo, MultipartFile arquivoReferencia) {
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
                                    .linhasReferencia(extrairLinhasReferencia(arquivoReferencia))
                                    .nomeArquivoReferencia(
                                            arquivoReferencia != null ? arquivoReferencia.getOriginalFilename() : null
                                    )
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

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> extrairLinhasReferencia(MultipartFile arquivoReferencia) {
        if (arquivoReferencia == null || arquivoReferencia.isEmpty()) {
            return null;
        }
        String tipoReferencia = determinarTipoArquivo(arquivoReferencia.getOriginalFilename());
        try (var in = arquivoReferencia.getInputStream()) {
            Planilha referencia = interpretadorPlanilhaService.lerPlanilha(
                    in,
                    arquivoReferencia.getOriginalFilename(),
                    tipoReferencia
            );
            Map<String, Object> dados = interpretadorPlanilhaService.extrairDadosEstruturados(referencia);
            List<Map<String, String>> linhas =
                    (List<Map<String, String>>) dados.getOrDefault("linhas", List.of());
            if (linhas.isEmpty()) {
                log.warn("Planilha referência enviada sem linhas úteis");
                return null;
            }
            log.info("Planilha referência carregada com {} linhas", linhas.size());
            return linhas;
        } catch (Exception e) {
            log.warn("Falha ao ler planilha referência. Seguindo sem referência: {}", e.getMessage());
            return null;
        }
    }
}
