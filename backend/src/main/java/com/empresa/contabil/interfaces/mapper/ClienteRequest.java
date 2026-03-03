package com.empresa.contabil.interfaces.mapper;

import com.empresa.contabil.domain.model.Cliente;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ClienteRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "CNPJ é obrigatório")
    private String documentNumber;

    private String estado;
    private String regime;

    public static ClienteRequest fromDomain(Cliente cliente) {
        return ClienteRequest.builder()
            .name(cliente.getName())
            .documentNumber(cliente.getDocumentNumber())
            .estado(cliente.getEstado())
            .regime(cliente.getRegime())
            .build();
    }
}
