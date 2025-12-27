package com.itau.app.transferencia.domain.exception;

public class ContaJaExisteException extends RuntimeException {
    public ContaJaExisteException(String message) {
        super(message);
    }
}
