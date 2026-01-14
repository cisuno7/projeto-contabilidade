package com.empresa.contabil.interfaces.mapper;

import com.empresa.contabil.domain.model.Cliente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ClienteRequest {

    private String name;
    private String documentNumber;

    public static ClienteRequest fromDomain(Cliente cliente) {
        return ClienteRequest.builder()
            .name(cliente.getName())
            .documentNumber(cliente.getDocumentNumber())
            .build();
    }
}
