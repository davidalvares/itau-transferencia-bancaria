package com.itau.app.transferencia.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transferencia {
    private UUID id;
    private String contaOrigem;
    private String contaDestino;
    private BigDecimal valor;
    private LocalDateTime dataHora;
    private StatusTransferencia status;
    private String mensagemErro;

    public enum StatusTransferencia {
        SUCESSO,
        ERRO
    }
}
