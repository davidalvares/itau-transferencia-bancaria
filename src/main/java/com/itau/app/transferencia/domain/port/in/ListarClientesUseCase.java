package com.itau.app.transferencia.domain.port.in;

import com.itau.app.transferencia.domain.model.Cliente;
import java.util.List;

public interface ListarClientesUseCase {
    List<Cliente> listar();
}
