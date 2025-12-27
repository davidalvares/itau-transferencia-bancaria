package com.itau.app.transferencia.infrastructure.adapter.out.persistence.repository;

import com.itau.app.transferencia.infrastructure.adapter.out.persistence.entity.TransferenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface JpaTransferenciaRepository extends JpaRepository<TransferenciaEntity, UUID> {
    List<TransferenciaEntity> findByContaOrigemOrContaDestinoOrderByDataHoraDesc(String contaOrigem,
            String contaDestino);
}
