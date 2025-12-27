package com.itau.app.transferencia.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "clientes")
@Data
public class ClienteEntity {
    @Id
    private UUID id;

    private String nome;

    @Column(unique = true)
    private String numeroConta;

    private BigDecimal saldo;

    @jakarta.persistence.Version
    private Long version;
}
