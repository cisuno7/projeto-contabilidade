package com.empresa.contabil.interfaces.rest;

import java.util.List;
import java.util.stream.Collectors;

import com.empresa.contabil.application.usecase.ClienteUseCase;
import com.empresa.contabil.interfaces.mapper.ClienteRequest;
import com.empresa.contabil.interfaces.mapper.ClienteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteUseCase clienteUseCase;

    @GetMapping
    public List<ClienteResponse> listar(@RequestParam(required = false) String nome) {
        var clientes = nome != null && !nome.isBlank()
                ? clienteUseCase.buscarPorNome(nome)
                : clienteUseCase.listarTodos();
        return clientes.stream()
                .map(ClienteResponse::fromDomain)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse criar(@Valid @RequestBody ClienteRequest request) {
        return ClienteResponse.fromDomain(clienteUseCase.criar(request));
    }
}
