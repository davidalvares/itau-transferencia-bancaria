package com.itau.app.transferencia.application.service;

import com.itau.app.transferencia.domain.model.Cliente;
import com.itau.app.transferencia.domain.port.in.BuscarClienteUseCase;
import com.itau.app.transferencia.domain.port.in.CadastroClienteUseCase;
import com.itau.app.transferencia.domain.port.in.ListarClientesUseCase;
import com.itau.app.transferencia.domain.port.out.ClienteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService implements CadastroClienteUseCase, ListarClientesUseCase, BuscarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    @Override
    public Cliente cadastrar(Cliente cliente) {
        if (clienteRepositoryPort.existeConta(cliente.getNumeroConta())) {
            throw new IllegalArgumentException("Conta já existe");
        }
        if (cliente.getId() == null) {
            cliente.setId(UUID.randomUUID());
        }
        return clienteRepositoryPort.salvar(cliente);
    }

    @Override
    public List<Cliente> listar() {
        return clienteRepositoryPort.listarTodos();
    }

    @Override
    public Optional<Cliente> buscarPorConta(String numeroConta) {
        return clienteRepositoryPort.buscarPorConta(numeroConta);
    }
}
