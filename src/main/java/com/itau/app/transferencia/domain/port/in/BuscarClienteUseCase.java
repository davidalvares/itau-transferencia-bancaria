package com.itau.app.transferencia.domain.port.in;

import com.itau.app.transferencia.domain.model.Cliente;
import java.util.Optional;

public interface BuscarClienteUseCase {
    Optional<Cliente> buscarPorConta(String numeroConta);
}
