package com.empresa.contabil.interfaces.mapper;

import com.empresa.contabil.application.dto.PlanilhaDTO;
import com.empresa.contabil.domain.model.Planilha;
import org.springframework.stereotype.Component;

@Component
public class PlanilhaDTOMapper {
    
    public PlanilhaDTO toDTO(Planilha planilha) {
        if (planilha == null) {
            return null;
        }
        
        return PlanilhaDTO.builder()
                .id(planilha.getId())
                .nomeArquivo(planilha.getNomeArquivo())
                .tipoArquivo(planilha.getTipoArquivo())
                .status(planilha.getStatus())
                .clienteId(planilha.getClienteId())
                .dataUpload(planilha.getDataUpload())
                .dataProcessamento(planilha.getDataProcessamento())

                //
                //Campos derivados do domínio (INTENÇÃO DE UI)
                .podeBaixar(planilha.getStatus().equals(Planilha.StatusPlanilha.PROCESSADA))
                .finalizada(planilha.getStatus().equals(Planilha.StatusPlanilha.CONCLUIDA))
                .emProcessamento(planilha.getStatus().equals(Planilha.StatusPlanilha.PROCESSANDO))
                
                .build();
    }
}
