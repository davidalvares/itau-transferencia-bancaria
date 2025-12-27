package com.itau.app.transferencia.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferenciaRequest(
        @NotNull String contaOrigem,
        @NotNull String contaDestino,
        @NotNull @Positive BigDecimal valor) {
}
