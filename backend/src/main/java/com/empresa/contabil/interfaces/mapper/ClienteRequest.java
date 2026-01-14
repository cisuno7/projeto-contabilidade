package com.empresa.contabil.interfaces.mapper;

import com.empresa.contabil.domain.model.Cliente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ClienteRequest {

    private String nome;
    private String cnpj;
    private String email;
    private String telefone;

    public static ClienteRequest fromDomain(Cliente cliente) {
        return ClienteRequest.builder()
            .nome(cliente.getNome())
            .cnpj(cliente.getCnpj())
            .email(cliente.getEmail())
            .telefone(cliente.getTelefone())
            .build();
    }
}
