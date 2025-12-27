package com.itau.app.transferencia.infrastructure.adapter.in.web;

import com.itau.app.transferencia.domain.model.Transferencia;
import com.itau.app.transferencia.domain.port.in.BuscarHistoricoTransferenciasUseCase;
import com.itau.app.transferencia.domain.port.in.RealizarTransferenciaUseCase;
import com.itau.app.transferencia.infrastructure.adapter.in.web.dto.TransferenciaRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/transferencias")
@RequiredArgsConstructor
public class TransferenciaController {

    private final RealizarTransferenciaUseCase realizarTransferenciaUseCase;
    private final BuscarHistoricoTransferenciasUseCase buscarHistoricoTransferenciasUseCase;

    @PostMapping
    public ResponseEntity<Transferencia> transferir(@RequestBody @Valid TransferenciaRequest request) {
        Transferencia transferencia = realizarTransferenciaUseCase.transferir(request.contaOrigem(),
                request.contaDestino(), request.valor());
        return ResponseEntity.status(HttpStatus.CREATED).body(transferencia);
    }

    @GetMapping("/conta/{numeroConta}")
    public ResponseEntity<List<Transferencia>> historico(@PathVariable String numeroConta) {
        return ResponseEntity.ok(buscarHistoricoTransferenciasUseCase.buscarHistorico(numeroConta));
    }
}
