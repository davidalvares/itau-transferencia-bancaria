package com.itau.app.transferencia.infrastructure.adapter.out.persistence;

import com.itau.app.transferencia.domain.model.Transferencia;
import com.itau.app.transferencia.domain.port.out.TransferenciaRepositoryPort;
import com.itau.app.transferencia.infrastructure.adapter.out.persistence.entity.TransferenciaEntity;
import com.itau.app.transferencia.infrastructure.adapter.out.persistence.repository.JpaTransferenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TransferenciaPersistenceAdapter implements TransferenciaRepositoryPort {

    private final JpaTransferenciaRepository jpaTransferenciaRepository;

    @Override
    public Transferencia salvar(Transferencia transferencia) {
        TransferenciaEntity entity = toEntity(transferencia);
        TransferenciaEntity saved = jpaTransferenciaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<Transferencia> buscarPorConta(String numeroConta) {
        return jpaTransferenciaRepository.findByContaOrigemOrContaDestinoOrderByDataHoraDesc(numeroConta, numeroConta)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private TransferenciaEntity toEntity(Transferencia domain) {
        TransferenciaEntity entity = new TransferenciaEntity();
        entity.setId(domain.getId());
        entity.setContaOrigem(domain.getContaOrigem());
        entity.setContaDestino(domain.getContaDestino());
        entity.setValor(domain.getValor());
        entity.setDataHora(domain.getDataHora());
        entity.setStatus(domain.getStatus().name());
        entity.setMensagemErro(domain.getMensagemErro());
        return entity;
    }

    private Transferencia toDomain(TransferenciaEntity entity) {
        return new Transferencia(
                entity.getId(),
                entity.getContaOrigem(),
                entity.getContaDestino(),
                entity.getValor(),
                entity.getDataHora(),
                Transferencia.StatusTransferencia.valueOf(entity.getStatus()),
                entity.getMensagemErro());
    }
}
