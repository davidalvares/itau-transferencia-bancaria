package com.itau.app.transferencia.domain.port.in;

import com.itau.app.transferencia.domain.model.Transferencia;
import java.util.List;

public interface BuscarHistoricoTransferenciasUseCase {
    List<Transferencia> buscarHistorico(String numeroConta);
}
