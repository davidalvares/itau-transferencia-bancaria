package com.itau.app.transferencia.application.service;

import com.itau.app.transferencia.domain.model.Cliente;
import com.itau.app.transferencia.domain.model.Transferencia;
import com.itau.app.transferencia.domain.exception.ContaNaoEncontradaException;
import com.itau.app.transferencia.domain.exception.LimiteTransferenciaExcedidoException;
import com.itau.app.transferencia.domain.exception.SaldoInsuficienteException;
import com.itau.app.transferencia.domain.constant.MensagensConstant;
import com.itau.app.transferencia.domain.port.in.BuscarHistoricoTransferenciasUseCase;
import com.itau.app.transferencia.domain.port.in.RealizarTransferenciaUseCase;
import com.itau.app.transferencia.domain.port.out.ClienteRepositoryPort;
import com.itau.app.transferencia.domain.port.out.TransferenciaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferenciaService implements RealizarTransferenciaUseCase, BuscarHistoricoTransferenciasUseCase {

    private final TransferenciaRepositoryPort transferenciaRepository;
    private final ClienteRepositoryPort clienteRepository;

    @Override
    @Transactional
    public Transferencia transferir(String contaOrigem, String contaDestino, BigDecimal valor) {
        log.info("Iniciando transferência. Origem: {}, Destino: {}, Valor: {}", contaOrigem, contaDestino, valor);
        Transferencia transferencia = new Transferencia();
        transferencia.setId(UUID.randomUUID());
        transferencia.setContaOrigem(contaOrigem);
        transferencia.setContaDestino(contaDestino);
        transferencia.setValor(valor);
        transferencia.setDataHora(LocalDateTime.now());

        if (valor.compareTo(new BigDecimal("10000")) > 0) {
            log.warn("Tentativa de transferência acima do limite: {}", valor);
            saveErro(transferencia, MensagensConstant.LIMITE_TRANSFERENCIA_EXCEDIDO);
            throw new LimiteTransferenciaExcedidoException(MensagensConstant.LIMITE_TRANSFERENCIA_EXCEDIDO);
        }

        Optional<Cliente> origemOpt = clienteRepository.buscarPorConta(contaOrigem);
        Optional<Cliente> destOpt = clienteRepository.buscarPorConta(contaDestino);

        if (origemOpt.isEmpty()) {
            log.error("Conta origem não encontrada: {}", contaOrigem);
            saveErro(transferencia, MensagensConstant.CONTA_ORIGEM_NAO_ENCONTRADA);
            throw new ContaNaoEncontradaException(MensagensConstant.CONTA_ORIGEM_NAO_ENCONTRADA);
        }

        if (destOpt.isEmpty()) {
            log.error("Conta destino não encontrada: {}", contaDestino);
            saveErro(transferencia, MensagensConstant.CONTA_DESTINO_NAO_ENCONTRADA);
            throw new ContaNaoEncontradaException(MensagensConstant.CONTA_DESTINO_NAO_ENCONTRADA);
        }

        Cliente origem = origemOpt.get();
        Cliente destino = destOpt.get();

        if (origem.getSaldo().compareTo(valor) < 0) {
            log.warn("Saldo insuficiente na conta origem: {}. Saldo atual: {}", contaOrigem, origem.getSaldo());
            saveErro(transferencia, MensagensConstant.SALDO_INSUFICIENTE);
            throw new SaldoInsuficienteException(MensagensConstant.SALDO_INSUFICIENTE);
        }

        // Realizar debito e credito
        origem.setSaldo(origem.getSaldo().subtract(valor));
        destino.setSaldo(destino.getSaldo().add(valor));

        clienteRepository.salvar(origem);
        clienteRepository.salvar(destino);

        transferencia.setStatus(Transferencia.StatusTransferencia.SUCESSO);
        Transferencia saved = transferenciaRepository.salvar(transferencia);
        log.info("Transferência realizada com sucesso. ID: {}", saved.getId());
        return saved;
    }

    @Override
    public List<Transferencia> buscarHistorico(String numeroConta) {
        log.info("Buscando histórico de transferências para conta: {}", numeroConta);
        List<Transferencia> historico = transferenciaRepository.buscarPorConta(numeroConta);
        log.info("Encontradas {} transferencias para conta {}", historico.size(), numeroConta);
        return historico;
    }

    private void saveErro(Transferencia transferencia, String mensagem) {
        transferencia.setStatus(Transferencia.StatusTransferencia.ERRO);
        transferencia.setMensagemErro(mensagem);
        transferenciaRepository.salvar(transferencia);
    }
}
