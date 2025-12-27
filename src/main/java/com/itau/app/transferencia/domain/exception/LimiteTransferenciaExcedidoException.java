package com.itau.app.transferencia.domain.exception;

public class LimiteTransferenciaExcedidoException extends RuntimeException {
    public LimiteTransferenciaExcedidoException(String message) {
        super(message);
    }
}
