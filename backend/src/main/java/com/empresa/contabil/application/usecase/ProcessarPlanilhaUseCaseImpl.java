package com.empresa.contabil.application.usecase;

import com.empresa.contabil.application.dto.PlanilhaDTO;
import com.empresa.contabil.application.dto.ProcessarPlanilhaRequest;
import com.empresa.contabil.domain.model.Planilha;
import com.empresa.contabil.domain.repository.PlanilhaRepository;
import com.empresa.contabil.domain.service.CorrecaoPlanilhaService;
import com.empresa.contabil.domain.service.InterpretadorPlanilhaService;
import com.empresa.contabil.infrastructure.filestorage.FileStorageService;
import com.empresa.contabil.domain.service.AIService;
import com.empresa.contabil.interfaces.mapper.PlanilhaDTOMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessarPlanilhaUseCaseImpl implements ProcessarPlanilhaUseCase {
    
    private final PlanilhaRepository planilhaRepository;
    private final AIService aiService;
    private final FileStorageService fileStorageService;
    private final InterpretadorPlanilhaService interpretadorPlanilhaService;
    private final CorrecaoPlanilhaService correcaoPlanilhaService;
    private final PlanilhaDTOMapper planilhaDTOMapper;
    
    @Override
    public PlanilhaDTO executar(ProcessarPlanilhaRequest request) {
        log.info("Iniciando processamento da planilha: {}", request.getPlanilhaId());
        
        Planilha planilha = planilhaRepository.buscarPorId(request.getPlanilhaId())
                .orElseThrow(() -> new RuntimeException("Planilha não encontrada: " + request.getPlanilhaId()));
        
        try {
            planilha.iniciarProcessamento();
            planilha = planilhaRepository.salvar(planilha);
            
            // Se o request não informar explicitamente, assumimos que usar IA = true
            Boolean flagRequest = request.getUsarIA();
            boolean usarIA = (flagRequest == null || Boolean.TRUE.equals(flagRequest)) && aiService.isDisponivel();
            log.info("Processando planilha {} com IA? {}", planilha.getId(), usarIA);
            
            // Leitura do arquivo físico e interpretação em campos
            if (fileStorageService.existe(planilha.getCaminhoArquivo())) {
                try (var inputStream = fileStorageService.ler(planilha.getCaminhoArquivo())) {
                    Planilha planilhaLida = interpretadorPlanilhaService.lerPlanilha(
                            inputStream,
                            planilha.getNomeArquivo(),
                            planilha.getTipoArquivo() != null ? planilha.getTipoArquivo() : "XLSX"
                    );
                    
                    // Copiar alguns metadados relevantes
                    planilha.setCampos(planilhaLida.getCampos());
                }
            } else {
                log.warn("Arquivo da planilha {} não encontrado em {}", planilha.getId(), planilha.getCaminhoArquivo());
            }
            
            // Validação básica da estrutura
            interpretadorPlanilhaService.validarEstrutura(planilha);
            
            // Correção de NCM/CEST (focado em SP + Simples Nacional)
            if (usarIA) {
                log.info("Aplicando correções automáticas de NCM/CEST (pipeline IA/regra) para planilha {}", planilha.getId());
                planilha = correcaoPlanilhaService.corrigirNcmECest(planilha);
            } else {
                log.info("Processando sem IA (apenas validação de estrutura) para planilha {}", planilha.getId());
            }
            
            // Gerar Excel corrigido e salvar
            byte[] excelBytes = interpretadorPlanilhaService.gerarExcel(planilha);
            String baseNome = planilha.getNomeArquivo() != null ? planilha.getNomeArquivo() : "planilha";
            if (!baseNome.toLowerCase().endsWith(".xlsx")) {
                baseNome = baseNome.replaceAll("\\.(xls|csv)$", "") + ".xlsx";
            }
            String nomeCorrigido = "corrigido_" + baseNome;
            String caminhoCorrigido = fileStorageService.salvarBytes(excelBytes, nomeCorrigido);
            
            
           // Incluir caminho do arquivo corrigido no ai_metadata como TEXTO
String alteracoes = planilha.getAiMetadata();

String novoMetadata;

if (alteracoes != null && !alteracoes.isBlank()) {
    novoMetadata = alteracoes + "\n\nArquivo gerado: " + caminhoCorrigido;
} else {
    novoMetadata = "Arquivo gerado: " + caminhoCorrigido;
}

planilha.setAiMetadata(novoMetadata);
            
            planilha.finalizarProcessamento();
            planilha = planilhaRepository.salvar(planilha);
            
            log.info("Planilha processada com sucesso: {}", planilha.getId());
            return planilhaDTOMapper.toDTO(planilha);
            
        } catch (Exception e) {
            log.error("Erro ao processar planilha", e);
            planilha.marcarComErro();
            planilhaRepository.salvar(planilha);
            throw new RuntimeException("Erro ao processar planilha: " + e.getMessage(), e);
        }
    }
}
