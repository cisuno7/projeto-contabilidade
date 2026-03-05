package com.empresa.contabil.interfaces.mapper;

import com.empresa.contabil.application.dto.PlanilhaDTO;
import com.empresa.contabil.domain.model.Planilha;
import com.empresa.contabil.domain.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanilhaDTOMapper {

    private final ClienteRepository clienteRepository;

    public PlanilhaDTO toDTO(Planilha planilha) {
        if (planilha == null) {
            return null;
        }

        String clienteNome = null;
        String clienteCnpj = null;
        if (planilha.getClienteId() != null) {
            var cliente = clienteRepository.buscarPorId(planilha.getClienteId());
            if (cliente.isPresent()) {
                clienteNome = cliente.get().getName();
                clienteCnpj = cliente.get().getDocumentNumber();
            }
        }

        return PlanilhaDTO.builder()
                .id(planilha.getId())
                .nomeArquivo(planilha.getNomeArquivo())
                .tipoArquivo(planilha.getTipoArquivo())
                .status(planilha.getStatus())
                .clienteId(planilha.getClienteId())
                .clienteNome(clienteNome)
                .clienteCnpj(clienteCnpj)
                .dataUpload(planilha.getDataUpload())
                .dataProcessamento(planilha.getDataProcessamento())
                

                // Campos derivados do domínio (INTENÇÃO DE UI)
                .podeBaixar(planilha.getStatus().equals(Planilha.StatusPlanilha.PROCESSADA) || planilha.getStatus().equals(Planilha.StatusPlanilha.CONCLUIDA))
                .temCorrigida(planilha.getCaminhoArquivoCorrigido() != null && !planilha.getCaminhoArquivoCorrigido().isBlank())
                .finalizada(planilha.getStatus().equals(Planilha.StatusPlanilha.CONCLUIDA))
                .emProcessamento(planilha.getStatus().equals(Planilha.StatusPlanilha.PROCESSANDO))
                
                .aiMetadata(planilha.getAiMetadata())
                .build();
    }
}
