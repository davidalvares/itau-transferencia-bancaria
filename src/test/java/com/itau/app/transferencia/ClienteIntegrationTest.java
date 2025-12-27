package com.itau.app.transferencia;

import com.itau.app.transferencia.domain.model.Cliente;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class ClienteIntegrationTest extends BaseIntegrationTest {

        @Test
        void deveCadastrarListarEBuscarCliente() throws Exception {
                // Cadastro
                Cliente novoCliente = new Cliente(null, "João Silva", "12345", new BigDecimal("1000.00"), null);

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(novoCliente)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.nome", is("João Silva")))
                                .andExpect(jsonPath("$.numeroConta", is("12345")))
                                .andExpect(jsonPath("$.saldo", is(1000.00)));

                // Listagem
                mockMvc.perform(get("/clientes"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].nome", is("João Silva")));

                // Busca por conta
                mockMvc.perform(get("/clientes/conta/12345"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nome", is("João Silva")));
        }

        @Test
        void deveCadastrarMultiplosClientes() throws Exception {
                // Cadastrar primeiro cliente
                Cliente cliente1 = new Cliente(null, "Maria Santos", "11111", new BigDecimal("2500.00"), null);
                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cliente1)))
                                .andExpect(status().isCreated());

                // Cadastrar segundo cliente
                Cliente cliente2 = new Cliente(null, "Pedro Oliveira", "22222", new BigDecimal("3500.00"), null);
                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cliente2)))
                                .andExpect(status().isCreated());

                // Cadastrar terceiro cliente
                Cliente cliente3 = new Cliente(null, "Ana Costa", "33333", new BigDecimal("1500.00"), null);
                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cliente3)))
                                .andExpect(status().isCreated());

                // Verificar listagem com 3 clientes
                mockMvc.perform(get("/clientes"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(3)));
        }

        @Test
        void deveFalharAoCadastrarClienteComContaDuplicada() throws Exception {
                // Cadastrar primeiro cliente
                Cliente cliente1 = new Cliente(null, "Carlos Silva", "99999", new BigDecimal("1000.00"), null);
                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cliente1)))
                                .andExpect(status().isCreated());

                // Tentar cadastrar outro cliente com a mesma conta
                Cliente cliente2 = new Cliente(null, "José Santos", "99999", new BigDecimal("2000.00"), null);
                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cliente2)))
                                .andExpect(status().is5xxServerError());
        }

        @Test
        void deveRetornarNotFoundParaContaInexistente() throws Exception {
                mockMvc.perform(get("/clientes/conta/99999"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void deveCadastrarClienteComSaldoZero() throws Exception {
                Cliente cliente = new Cliente(null, "Cliente Sem Saldo", "55555", BigDecimal.ZERO, null);

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cliente)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.saldo", is(0)));
        }

        @Test
        void deveCadastrarClienteComSaldoAlto() throws Exception {
                Cliente cliente = new Cliente(null, "Cliente Rico", "77777", new BigDecimal("999999.99"), null);

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cliente)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.saldo", is(999999.99)));
        }

        @Test
        void deveListarClientesVazioQuandoNaoHouverClientes() throws Exception {
                mockMvc.perform(get("/clientes"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        void deveBuscarClientePorContaAposCadastro() throws Exception {
                Cliente cliente = new Cliente(null, "Teste Busca", "44444", new BigDecimal("5000.00"), null);

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cliente)))
                                .andExpect(status().isCreated());

                mockMvc.perform(get("/clientes/conta/44444"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nome", is("Teste Busca")))
                                .andExpect(jsonPath("$.numeroConta", is("44444")))
                                .andExpect(jsonPath("$.saldo", is(5000.00)));
        }
}
