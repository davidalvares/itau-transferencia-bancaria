package com.itau.app.transferencia.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    private UUID id;
    private String nome;
    private String numeroConta;
    private BigDecimal saldo;
    private Long version;
}
