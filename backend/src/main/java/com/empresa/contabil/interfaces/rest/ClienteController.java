package com.empresa.contabil.interfaces.rest;

import java.util.List;
import java.util.stream.Collectors;

import com.empresa.contabil.application.usecase.ClienteUseCase;
import com.empresa.contabil.interfaces.mapper.ClienteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteUseCase clienteUseCase;

    @GetMapping
    public List<ClienteResponse> listar() {
        return clienteUseCase.listarTodos()
                .stream()
                .map(ClienteResponse::fromDomain)
                .collect(Collectors.toList());
    }
}
