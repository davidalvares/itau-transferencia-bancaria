package com.itau.app.transferencia.domain.port.out;

import com.itau.app.transferencia.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {
    Cliente salvar(Cliente cliente);

    List<Cliente> listarTodos();

    Optional<Cliente> buscarPorConta(String numeroConta);

    boolean existeConta(String numeroConta);
}
