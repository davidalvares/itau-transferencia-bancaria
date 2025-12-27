package com.itau.app.transferencia.application.service;

import com.itau.app.transferencia.domain.constant.MensagensConstant;
import com.itau.app.transferencia.domain.model.Cliente;
import com.itau.app.transferencia.domain.port.out.ClienteRepositoryPort;
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
class ClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    @DisplayName("Deve cadastrar cliente com sucesso quando conta não existe")
    void deveCadastrarClienteComSucesso() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setNome("João Silva");
        cliente.setNumeroConta("12345-6");
        cliente.setSaldo(BigDecimal.ZERO);

        when(clienteRepositoryPort.existeConta("12345-6")).thenReturn(false);
        when(clienteRepositoryPort.salvar(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente c = invocation.getArgument(0);
            if (c.getId() == null)
                c.setId(UUID.randomUUID());
            return c;
        });

        // Act
        Cliente resultado = clienteService.cadastrar(cliente);

        // Assert
        assertNotNull(resultado.getId());
        assertEquals("João Silva", resultado.getNome());
        verify(clienteRepositoryPort).existeConta("12345-6");
        verify(clienteRepositoryPort).salvar(cliente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar cliente com conta existente")
    void deveLancarExcecaoQuandoContaJaExiste() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setNumeroConta("12345-6");

        when(clienteRepositoryPort.existeConta("12345-6")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.cadastrar(cliente);
        });

        assertEquals(MensagensConstant.CONTA_JA_EXISTE, exception.getMessage());
        verify(clienteRepositoryPort, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve listar todos os clientes")
    void deveListarTodosClientes() {
        // Arrange
        Cliente c1 = new Cliente();
        Cliente c2 = new Cliente();
        when(clienteRepositoryPort.listarTodos()).thenReturn(List.of(c1, c2));

        // Act
        List<Cliente> resultado = clienteService.listar();

        // Assert
        assertEquals(2, resultado.size());
        verify(clienteRepositoryPort).listarTodos();
    }

    @Test
    @DisplayName("Deve buscar cliente por conta")
    void deveBuscarClientePorConta() {
        // Arrange
        String numeroConta = "12345-6";
        Cliente cliente = new Cliente();
        cliente.setNumeroConta(numeroConta);
        when(clienteRepositoryPort.buscarPorConta(numeroConta)).thenReturn(Optional.of(cliente));

        // Act
        Optional<Cliente> resultado = clienteService.buscarPorConta(numeroConta);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(numeroConta, resultado.get().getNumeroConta());
        verify(clienteRepositoryPort).buscarPorConta(numeroConta);
    }
}
