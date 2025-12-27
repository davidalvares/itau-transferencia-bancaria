package com.itau.app.transferencia.infrastructure.adapter.out.persistence.repository;

import com.itau.app.transferencia.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface JpaClienteRepository extends JpaRepository<ClienteEntity, UUID> {
    Optional<ClienteEntity> findByNumeroConta(String numeroConta);

    boolean existsByNumeroConta(String numeroConta);
}
