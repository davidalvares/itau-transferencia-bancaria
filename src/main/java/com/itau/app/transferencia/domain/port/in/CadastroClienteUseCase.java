package com.itau.app.transferencia.domain.port.in;

import com.itau.app.transferencia.domain.model.Cliente;

public interface CadastroClienteUseCase {
    Cliente cadastrar(Cliente cliente);
}
