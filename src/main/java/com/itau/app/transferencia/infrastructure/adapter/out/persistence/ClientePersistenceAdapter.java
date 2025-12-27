package com.itau.app.transferencia.infrastructure.adapter.out.persistence;

import com.itau.app.transferencia.domain.model.Cliente;
import com.itau.app.transferencia.domain.port.out.ClienteRepositoryPort;
import com.itau.app.transferencia.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import com.itau.app.transferencia.infrastructure.adapter.out.persistence.repository.JpaClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClientePersistenceAdapter implements ClienteRepositoryPort {

    private final JpaClienteRepository jpaClienteRepository;

    @Override
    public Cliente salvar(Cliente cliente) {
        ClienteEntity entity = toEntity(cliente);
        ClienteEntity saved = jpaClienteRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<Cliente> listarTodos() {
        return jpaClienteRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Cliente> buscarPorConta(String numeroConta) {
        return jpaClienteRepository.findByNumeroConta(numeroConta)
                .map(this::toDomain);
    }

    @Override
    public boolean existeConta(String numeroConta) {
        return jpaClienteRepository.existsByNumeroConta(numeroConta);
    }

    private ClienteEntity toEntity(Cliente domain) {
        ClienteEntity entity = new ClienteEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setNumeroConta(domain.getNumeroConta());
        entity.setSaldo(domain.getSaldo());
        entity.setVersion(domain.getVersion());
        return entity;
    }

    private Cliente toDomain(ClienteEntity entity) {
        return new Cliente(entity.getId(), entity.getNome(), entity.getNumeroConta(), entity.getSaldo(),
                entity.getVersion());
    }
}
