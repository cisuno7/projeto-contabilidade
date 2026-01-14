package com.empresa.contabil.application.usecase;

import java.util.List;

import com.empresa.contabil.domain.model.Cliente;
import com.empresa.contabil.domain.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteUseCase {

    private final ClienteRepository clienteRepository;

    public List<Cliente> listarTodos() {
        return clienteRepository.buscarTodos();
    }
}
