package com.itau.app.transferencia.infrastructure.adapter.in.web;

import com.itau.app.transferencia.domain.model.Cliente;
import com.itau.app.transferencia.domain.port.in.BuscarClienteUseCase;
import com.itau.app.transferencia.domain.port.in.CadastroClienteUseCase;
import com.itau.app.transferencia.domain.port.in.ListarClientesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final CadastroClienteUseCase cadastroClienteUseCase;
    private final ListarClientesUseCase listarClientesUseCase;
    private final BuscarClienteUseCase buscarClienteUseCase;

    @PostMapping
    public ResponseEntity<Cliente> cadastrar(@RequestBody Cliente cliente) {
        Cliente novoCliente = cadastroClienteUseCase.cadastrar(cliente);
        return ResponseEntity.created(URI.create("/clientes/conta/" + novoCliente.getNumeroConta())).body(novoCliente);
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        return ResponseEntity.ok(listarClientesUseCase.listar());
    }

    @GetMapping("/conta/{numeroConta}")
    public ResponseEntity<Cliente> buscarPorConta(@PathVariable String numeroConta) {
        return buscarClienteUseCase.buscarPorConta(numeroConta)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
