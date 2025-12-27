package com.itau.app.transferencia.domain.port.out;

import com.itau.app.transferencia.domain.model.Transferencia;
import java.util.List;

public interface TransferenciaRepositoryPort {
    Transferencia salvar(Transferencia transferencia);

    List<Transferencia> buscarPorConta(String numeroConta);
}
