package com.empresa.contabil.application.usecase;

import java.util.List;

import com.empresa.contabil.domain.model.Cliente;
import com.empresa.contabil.domain.repository.ClienteRepository;
import com.empresa.contabil.interfaces.mapper.ClienteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteUseCase {

    private final ClienteRepository clienteRepository;

    public List<Cliente> listarTodos() {
        return clienteRepository.buscarTodos();
    }

    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.buscarPorNomeContendo(nome);
    }

    public Cliente criar(ClienteRequest request) {
        if (request.getDocumentNumber() != null && clienteRepository.existePorCnpj(request.getDocumentNumber())) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com este CNPJ.");
        }
        Cliente cliente = Cliente.builder()
                .name(request.getName())
                .documentNumber(request.getDocumentNumber())
                .estado(request.getEstado())
                .regime(request.getRegime())
                .active(true)
                .build();
        return clienteRepository.salvar(cliente);
    }
}
