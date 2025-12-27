package com.itau.app.transferencia.domain.port.in;

import com.itau.app.transferencia.domain.model.Transferencia;
import java.math.BigDecimal;

public interface RealizarTransferenciaUseCase {
    Transferencia transferir(String contaOrigem, String contaDestino, BigDecimal valor);
}
