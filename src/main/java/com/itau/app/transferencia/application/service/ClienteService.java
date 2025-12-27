package com.itau.app.transferencia.application.service;

import com.itau.app.transferencia.domain.constant.MensagensConstant;
import com.itau.app.transferencia.domain.model.Cliente;
import com.itau.app.transferencia.domain.port.in.BuscarClienteUseCase;
import com.itau.app.transferencia.domain.port.in.CadastroClienteUseCase;
import com.itau.app.transferencia.domain.port.in.ListarClientesUseCase;
import com.itau.app.transferencia.domain.port.out.ClienteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteService implements CadastroClienteUseCase, ListarClientesUseCase, BuscarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    @Override
    public Cliente cadastrar(Cliente cliente) {
        log.info("Iniciando cadastro de cliente: {}", cliente.getNome());
        if (clienteRepositoryPort.existeConta(cliente.getNumeroConta())) {
            log.error("Tentativa de cadastro com conta já existente: {}", cliente.getNumeroConta());
            throw new IllegalArgumentException(MensagensConstant.CONTA_JA_EXISTE);
        }
        if (cliente.getId() == null) {
            cliente.setId(UUID.randomUUID());
        }
        Cliente novoCliente = clienteRepositoryPort.salvar(cliente);
        log.info("Cliente cadastrado com sucesso. ID: {}", novoCliente.getId());
        return novoCliente;
    }

    @Override
    public List<Cliente> listar() {
        log.info("Listando todos os clientes");
        List<Cliente> clientes = clienteRepositoryPort.listarTodos();
        log.info("Total de clientes encontrados: {}", clientes.size());
        return clientes;
    }

    @Override
    public Optional<Cliente> buscarPorConta(String numeroConta) {
        log.info("Buscando cliente pela conta: {}", numeroConta);
        return clienteRepositoryPort.buscarPorConta(numeroConta);
    }
}
