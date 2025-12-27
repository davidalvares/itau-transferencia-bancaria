package com.itau.app.transferencia.application.service;

import com.itau.app.transferencia.domain.model.Cliente;
import com.itau.app.transferencia.domain.model.Transferencia;
import com.itau.app.transferencia.domain.exception.ContaNaoEncontradaException;
import com.itau.app.transferencia.domain.exception.LimiteTransferenciaExcedidoException;
import com.itau.app.transferencia.domain.exception.SaldoInsuficienteException;
import com.itau.app.transferencia.domain.port.in.BuscarHistoricoTransferenciasUseCase;
import com.itau.app.transferencia.domain.port.in.RealizarTransferenciaUseCase;
import com.itau.app.transferencia.domain.port.out.ClienteRepositoryPort;
import com.itau.app.transferencia.domain.port.out.TransferenciaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferenciaService implements RealizarTransferenciaUseCase, BuscarHistoricoTransferenciasUseCase {

    private final TransferenciaRepositoryPort transferenciaRepository;
    private final ClienteRepositoryPort clienteRepository;

    @Override
    @Transactional
    public Transferencia transferir(String contaOrigem, String contaDestino, BigDecimal valor) {
        Transferencia transferencia = new Transferencia();
        transferencia.setId(UUID.randomUUID());
        transferencia.setContaOrigem(contaOrigem);
        transferencia.setContaDestino(contaDestino);
        transferencia.setValor(valor);
        transferencia.setDataHora(LocalDateTime.now());

        if (valor.compareTo(new BigDecimal("10000")) > 0) {
            saveErro(transferencia, "Valor excede o limite de R$ 10.000,00");
            throw new LimiteTransferenciaExcedidoException("Valor excede o limite de R$ 10.000,00");
        }

        Optional<Cliente> origemOpt = clienteRepository.buscarPorConta(contaOrigem);
        Optional<Cliente> destOpt = clienteRepository.buscarPorConta(contaDestino);

        if (origemOpt.isEmpty()) {
            saveErro(transferencia, "Conta de origem não encontrada");
            throw new ContaNaoEncontradaException("Conta de origem não encontrada");
        }

        if (destOpt.isEmpty()) {
            saveErro(transferencia, "Conta de destino não encontrada");
            throw new ContaNaoEncontradaException("Conta de destino não encontrada");
        }

        Cliente origem = origemOpt.get();
        Cliente destino = destOpt.get();

        if (origem.getSaldo().compareTo(valor) < 0) {
            saveErro(transferencia, "Saldo insuficiente");
            throw new SaldoInsuficienteException("Saldo insuficiente");
        }

        // Realizar debito e credito
        origem.setSaldo(origem.getSaldo().subtract(valor));
        destino.setSaldo(destino.getSaldo().add(valor));

        clienteRepository.salvar(origem);
        clienteRepository.salvar(destino);

        transferencia.setStatus(Transferencia.StatusTransferencia.SUCESSO);
        return transferenciaRepository.salvar(transferencia);
    }

    @Override
    public List<Transferencia> buscarHistorico(String numeroConta) {
        return transferenciaRepository.buscarPorConta(numeroConta);
    }

    private void saveErro(Transferencia transferencia, String mensagem) {
        transferencia.setStatus(Transferencia.StatusTransferencia.ERRO);
        transferencia.setMensagemErro(mensagem);
        transferenciaRepository.salvar(transferencia);
    }
}
