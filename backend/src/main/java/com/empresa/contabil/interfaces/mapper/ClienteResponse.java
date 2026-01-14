package com.empresa.contabil.interfaces.mapper;

import com.empresa.contabil.domain.model.Cliente;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ClienteResponse {

    private UUID id;
    private String name;
    private String documentNumber;
    private Boolean active;

    public static ClienteResponse fromDomain(Cliente cliente) {
        return new ClienteResponse(
            cliente.getId(),
            cliente.getName(),
            cliente.getDocumentNumber(),
            cliente.getActive()
        );
    }
}
