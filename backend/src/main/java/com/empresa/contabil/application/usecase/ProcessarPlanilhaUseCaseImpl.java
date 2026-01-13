package com.empresa.contabil.application.usecase;

import com.empresa.contabil.application.dto.PlanilhaDTO;
import com.empresa.contabil.application.dto.ProcessarPlanilhaRequest;
import com.empresa.contabil.domain.model.Planilha;
import com.empresa.contabil.domain.repository.PlanilhaRepository;
import com.empresa.contabil.domain.service.AIService;
import com.empresa.contabil.interfaces.mapper.PlanilhaDTOMapper;
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
    private final PlanilhaDTOMapper planilhaDTOMapper;
    
    @Override
    public PlanilhaDTO executar(ProcessarPlanilhaRequest request) {
        log.info("Iniciando processamento da planilha: {}", request.getPlanilhaId());
        
        Planilha planilha = planilhaRepository.buscarPorId(request.getPlanilhaId())
                .orElseThrow(() -> new RuntimeException("Planilha não encontrada: " + request.getPlanilhaId()));
        
        try {
            planilha.iniciarProcessamento();
            planilha = planilhaRepository.salvar(planilha);
            
            // TODO: Implementar lógica real de processamento
            // Por enquanto, apenas simula o processamento
            if (request.getUsarIA() != null && request.getUsarIA() && aiService.isDisponivel()) {
                log.info("Processando com IA...");
                // Implementar processamento com IA
            } else {
                log.info("Processando sem IA...");
            }
            
            // TODO: Implementar processamento real aqui
            
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
