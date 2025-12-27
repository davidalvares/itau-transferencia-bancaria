package com.itau.app.transferencia.application.service;

import com.itau.app.transferencia.domain.constant.MensagensConstant;
import com.itau.app.transferencia.domain.exception.ContaNaoEncontradaException;
import com.itau.app.transferencia.domain.exception.LimiteTransferenciaExcedidoException;
import com.itau.app.transferencia.domain.exception.SaldoInsuficienteException;
import com.itau.app.transferencia.domain.model.Cliente;
import com.itau.app.transferencia.domain.model.Transferencia;
import com.itau.app.transferencia.domain.port.out.ClienteRepositoryPort;
import com.itau.app.transferencia.domain.port.out.TransferenciaRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferenciaServiceTest {

    @Mock
    private TransferenciaRepositoryPort transferenciaRepository;

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @InjectMocks
    private TransferenciaService transferenciaService;

    @Test
    @DisplayName("Deve realizar transferência com sucesso")
    void deveRealizarTransferenciaComSucesso() {
        // Arrange
        String contaOrigem = "11111-1";
        String contaDestino = "22222-2";
        BigDecimal valor = new BigDecimal("100.00");

        Cliente origem = new Cliente();
        origem.setNumeroConta(contaOrigem);
        origem.setSaldo(new BigDecimal("500.00"));

        Cliente destino = new Cliente();
        destino.setNumeroConta(contaDestino);
        destino.setSaldo(new BigDecimal("200.00"));

        when(clienteRepository.buscarPorConta(contaOrigem)).thenReturn(Optional.of(origem));
        when(clienteRepository.buscarPorConta(contaDestino)).thenReturn(Optional.of(destino));
        when(transferenciaRepository.salvar(any(Transferencia.class))).thenAnswer(i -> {
            Transferencia t = i.getArgument(0);
            return t; // retorna a propria instancia
        });

        // Act
        Transferencia resultado = transferenciaService.transferir(contaOrigem, contaDestino, valor);

        // Assert
        assertEquals(Transferencia.StatusTransferencia.SUCESSO, resultado.getStatus());
        assertEquals(new BigDecimal("400.00"), origem.getSaldo());
        assertEquals(new BigDecimal("300.00"), destino.getSaldo());

        verify(clienteRepository).salvar(origem);
        verify(clienteRepository).salvar(destino);
        verify(transferenciaRepository).salvar(any(Transferencia.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando valor excede 10.000")
    void deveLancarExcecaoLimiteExcedido() {
        // Arrange
        BigDecimal valor = new BigDecimal("10000.01");

        // Act & Assert
        LimiteTransferenciaExcedidoException ex = assertThrows(LimiteTransferenciaExcedidoException.class, () -> {
            transferenciaService.transferir("origin", "dest", valor);
        });

        assertEquals(MensagensConstant.LIMITE_TRANSFERENCIA_EXCEDIDO, ex.getMessage());

        // Verifica se salvou erro
        verify(transferenciaRepository).salvar(argThat(t -> t.getStatus() == Transferencia.StatusTransferencia.ERRO &&
                t.getMensagemErro().equals(MensagensConstant.LIMITE_TRANSFERENCIA_EXCEDIDO)));

        // Nao deve buscar clientes nem salvar saldos
        verify(clienteRepository, never()).buscarPorConta(anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando conta origem não encontrada")
    void deveLancarExcecaoOrigemNaoEncontrada() {
        // Arrange
        String contaOrigem = "00000-0";
        String contaDestino = "11111-1";
        BigDecimal valor = new BigDecimal("500.00");

        when(clienteRepository.buscarPorConta(contaOrigem)).thenReturn(Optional.empty());
        // dest pode ser chamada ou nao, depende da ordem, mas origem falha primeiro

        // Act & Assert
        ContaNaoEncontradaException ex = assertThrows(ContaNaoEncontradaException.class, () -> {
            transferenciaService.transferir(contaOrigem, contaDestino, valor);
        });

        assertEquals(MensagensConstant.CONTA_ORIGEM_NAO_ENCONTRADA, ex.getMessage());

        // Verifica erro salvo
        verify(transferenciaRepository).salvar(argThat(t -> t.getStatus() == Transferencia.StatusTransferencia.ERRO &&
                t.getMensagemErro().equals(MensagensConstant.CONTA_ORIGEM_NAO_ENCONTRADA)));
    }

    @Test
    @DisplayName("Deve lançar exceção quando conta destino não encontrada")
    void deveLancarExcecaoDestinoNaoEncontrada() {
        // Arrange
        String contaOrigem = "11111-1";
        String contaDestino = "99999-9";
        BigDecimal valor = new BigDecimal("500.00");

        when(clienteRepository.buscarPorConta(contaOrigem)).thenReturn(Optional.of(new Cliente()));
        when(clienteRepository.buscarPorConta(contaDestino)).thenReturn(Optional.empty());

        // Act & Assert
        ContaNaoEncontradaException ex = assertThrows(ContaNaoEncontradaException.class, () -> {
            transferenciaService.transferir(contaOrigem, contaDestino, valor);
        });

        assertEquals(MensagensConstant.CONTA_DESTINO_NAO_ENCONTRADA, ex.getMessage());

        verify(transferenciaRepository).salvar(argThat(t -> t.getStatus() == Transferencia.StatusTransferencia.ERRO &&
                t.getMensagemErro().equals(MensagensConstant.CONTA_DESTINO_NAO_ENCONTRADA)));
    }

    @Test
    @DisplayName("Deve lançar exceção quando saldo insuficiente")
    void deveLancarExcecaoSaldoInsuficiente() {
        // Arrange
        String contaOrigem = "11111-1";
        String contaDestino = "22222-2";
        BigDecimal valor = new BigDecimal("1000.00");

        Cliente origem = new Cliente();
        origem.setSaldo(new BigDecimal("100.00")); // Saldo menor que valor
        Cliente destino = new Cliente();

        when(clienteRepository.buscarPorConta(contaOrigem)).thenReturn(Optional.of(origem));
        when(clienteRepository.buscarPorConta(contaDestino)).thenReturn(Optional.of(destino));

        // Act & Assert
        SaldoInsuficienteException ex = assertThrows(SaldoInsuficienteException.class, () -> {
            transferenciaService.transferir(contaOrigem, contaDestino, valor);
        });

        assertEquals(MensagensConstant.SALDO_INSUFICIENTE, ex.getMessage());

        verify(transferenciaRepository).salvar(argThat(t -> t.getStatus() == Transferencia.StatusTransferencia.ERRO &&
                t.getMensagemErro().equals(MensagensConstant.SALDO_INSUFICIENTE)));

        // Saldos nao devem ter mudado
        assertEquals(new BigDecimal("100.00"), origem.getSaldo());
    }

    @Test
    @DisplayName("Deve buscar histórico de transferências")
    void deveBuscarHistorico() {
        // Arrange
        String conta = "12345";
        Transferencia t1 = new Transferencia();
        Transferencia t2 = new Transferencia();
        when(transferenciaRepository.buscarPorConta(conta)).thenReturn(List.of(t1, t2));

        // Act
        List<Transferencia> historico = transferenciaService.buscarHistorico(conta);

        // Assert
        assertEquals(2, historico.size());
        verify(transferenciaRepository).buscarPorConta(conta);
    }
}
