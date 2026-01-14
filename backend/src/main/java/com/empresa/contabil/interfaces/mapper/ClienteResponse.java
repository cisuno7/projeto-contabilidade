package com.empresa.contabil.interfaces.mapper;

import com.empresa.contabil.domain.model.Cliente;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ClienteResponse {

    private UUID id;
    private String nome;
    private String cnpj;

    public static ClienteResponse fromDomain(Cliente cliente) {
        return new ClienteResponse(
            cliente.getId(),
            cliente.getNome(),
            cliente.getCnpj()
        );
    }
}
