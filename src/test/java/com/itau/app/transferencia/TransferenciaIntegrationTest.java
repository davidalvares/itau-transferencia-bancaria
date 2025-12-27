package com.itau.app.transferencia;

import com.itau.app.transferencia.domain.model.Cliente;
import com.itau.app.transferencia.infrastructure.adapter.in.web.dto.TransferenciaRequest;
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
public class TransferenciaIntegrationTest extends BaseIntegrationTest {

        @Test
        void deveRealizarTransferenciaEBuscarHistorico() throws Exception {
                // Criar clientes
                Cliente origem = new Cliente(null, "Maria", "99999", new BigDecimal("5000.00"), null);
                Cliente destino = new Cliente(null, "Pedro", "88888", new BigDecimal("1000.00"), null);

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(origem)))
                                .andExpect(status().isCreated());

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(destino)))
                                .andExpect(status().isCreated());

                // Realizar Transferência com Sucesso
                TransferenciaRequest request = new TransferenciaRequest("99999", "88888", new BigDecimal("1000.00"));

                mockMvc.perform(post("/transferencias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status", is("SUCESSO")));

                // Verificar Saldos
                mockMvc.perform(get("/clientes/conta/99999"))
                                .andExpect(jsonPath("$.saldo", is(4000.00)));

                mockMvc.perform(get("/clientes/conta/88888"))
                                .andExpect(jsonPath("$.saldo", is(2000.00)));

                // Verificar Histórico
                mockMvc.perform(get("/transferencias/conta/99999"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].valor", is(1000.00)));
        }

        @Test
        void deveFalharTransferenciaSemSaldo() throws Exception {
                // Criar clientes
                Cliente origem = new Cliente(null, "Sem Grana", "11111", new BigDecimal("500.00"), null);
                Cliente destino = new Cliente(null, "Rico", "22222", new BigDecimal("1000.00"), null);

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(origem)))
                                .andExpect(status().isCreated());

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(destino)))
                                .andExpect(status().isCreated());

                // Tentar transferir valor maior que saldo
                TransferenciaRequest request = new TransferenciaRequest("11111", "22222", new BigDecimal("1000.00"));

                mockMvc.perform(post("/transferencias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message", containsString("Saldo insuficiente")));
        }

        @Test
        void deveFalharTransferenciaAcimaDoLimite() throws Exception {
                // Criar clientes com saldo alto
                Cliente origem = new Cliente(null, "Milionário", "33333", new BigDecimal("50000.00"), null);
                Cliente destino = new Cliente(null, "Destinatário", "44444", new BigDecimal("1000.00"), null);

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(origem)))
                                .andExpect(status().isCreated());

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(destino)))
                                .andExpect(status().isCreated());

                // Tentar transferir valor acima do limite de R$ 10.000,00
                TransferenciaRequest request = new TransferenciaRequest("33333", "44444", new BigDecimal("15000.00"));

                mockMvc.perform(post("/transferencias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message",
                                                containsString("Valor excede o limite de R$ 10.000,00")));
        }

        @Test
        void deveFalharTransferenciaComContaOrigemInexistente() throws Exception {
                // Criar apenas conta destino
                Cliente destino = new Cliente(null, "Destinatário", "55555", new BigDecimal("1000.00"), null);

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(destino)))
                                .andExpect(status().isCreated());

                // Tentar transferir de conta inexistente
                TransferenciaRequest request = new TransferenciaRequest("99999", "55555", new BigDecimal("100.00"));

                mockMvc.perform(post("/transferencias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message", containsString("Conta de origem não encontrada")));
        }

        @Test
        void deveFalharTransferenciaComContaDestinoInexistente() throws Exception {
                // Criar apenas conta origem
                Cliente origem = new Cliente(null, "Remetente", "66666", new BigDecimal("5000.00"), null);

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(origem)))
                                .andExpect(status().isCreated());

                // Tentar transferir para conta inexistente
                TransferenciaRequest request = new TransferenciaRequest("66666", "99999", new BigDecimal("100.00"));

                mockMvc.perform(post("/transferencias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message", containsString("Conta de destino não encontrada")));
        }

        @Test
        void deveRealizarTransferenciaNoLimiteExato() throws Exception {
                // Criar clientes
                Cliente origem = new Cliente(null, "Cliente A", "77777", new BigDecimal("15000.00"), null);
                Cliente destino = new Cliente(null, "Cliente B", "88888", new BigDecimal("1000.00"), null);

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(origem)))
                                .andExpect(status().isCreated());

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(destino)))
                                .andExpect(status().isCreated());

                // Transferir exatamente R$ 10.000,00 (limite máximo)
                TransferenciaRequest request = new TransferenciaRequest("77777", "88888", new BigDecimal("10000.00"));

                mockMvc.perform(post("/transferencias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status", is("SUCESSO")))
                                .andExpect(jsonPath("$.valor", is(10000.00)));

                // Verificar saldos
                mockMvc.perform(get("/clientes/conta/77777"))
                                .andExpect(jsonPath("$.saldo", is(5000.00)));

                mockMvc.perform(get("/clientes/conta/88888"))
                                .andExpect(jsonPath("$.saldo", is(11000.00)));
        }

        @Test
        void deveRealizarMultiplasTransferencias() throws Exception {
                // Criar clientes
                Cliente origem = new Cliente(null, "Transferidor", "10001", new BigDecimal("10000.00"), null);
                Cliente destino = new Cliente(null, "Recebedor", "10002", new BigDecimal("1000.00"), null);

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(origem)))
                                .andExpect(status().isCreated());

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(destino)))
                                .andExpect(status().isCreated());

                // Primeira transferência
                TransferenciaRequest request1 = new TransferenciaRequest("10001", "10002", new BigDecimal("1000.00"));
                mockMvc.perform(post("/transferencias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request1)))
                                .andExpect(status().isOk());

                // Segunda transferência
                TransferenciaRequest request2 = new TransferenciaRequest("10001", "10002", new BigDecimal("2000.00"));
                mockMvc.perform(post("/transferencias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request2)))
                                .andExpect(status().isOk());

                // Terceira transferência
                TransferenciaRequest request3 = new TransferenciaRequest("10001", "10002", new BigDecimal("500.00"));
                mockMvc.perform(post("/transferencias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request3)))
                                .andExpect(status().isOk());

                // Verificar histórico com 3 transferências
                mockMvc.perform(get("/transferencias/conta/10001"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(3)));

                // Verificar saldo final
                mockMvc.perform(get("/clientes/conta/10001"))
                                .andExpect(jsonPath("$.saldo", is(6500.00)));

                mockMvc.perform(get("/clientes/conta/10002"))
                                .andExpect(jsonPath("$.saldo", is(4500.00)));
        }

        @Test
        void deveRegistrarTransferenciaFalhadaNoHistorico() throws Exception {
                // Criar clientes
                Cliente origem = new Cliente(null, "Pobre", "20001", new BigDecimal("100.00"), null);
                Cliente destino = new Cliente(null, "Destinatário", "20002", new BigDecimal("1000.00"), null);

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(origem)))
                                .andExpect(status().isCreated());

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(destino)))
                                .andExpect(status().isCreated());

                // Tentar transferência que vai falhar
                TransferenciaRequest request = new TransferenciaRequest("20001", "20002", new BigDecimal("500.00"));

                mockMvc.perform(post("/transferencias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());

                // Verificar que a transferência com erro está no histórico
                mockMvc.perform(get("/transferencias/conta/20001"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].status", is("ERRO")))
                                .andExpect(jsonPath("$[0].mensagemErro", containsString("Saldo insuficiente")));
        }

        @Test
        void deveRetornarHistoricoVazioParaContaSemTransferencias() throws Exception {
                // Criar cliente
                Cliente cliente = new Cliente(null, "Sem Movimentação", "30001", new BigDecimal("5000.00"), null);

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cliente)))
                                .andExpect(status().isCreated());

                // Verificar histórico vazio
                mockMvc.perform(get("/transferencias/conta/30001"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        void deveOrdenarHistoricoPorDataDecrescente() throws Exception {
                // Criar clientes
                Cliente origem = new Cliente(null, "Origem", "40001", new BigDecimal("10000.00"), null);
                Cliente destino = new Cliente(null, "Destino", "40002", new BigDecimal("1000.00"), null);

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(origem)))
                                .andExpect(status().isCreated());

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(destino)))
                                .andExpect(status().isCreated());

                // Realizar várias transferências
                for (int i = 1; i <= 5; i++) {
                        TransferenciaRequest request = new TransferenciaRequest("40001", "40002",
                                        new BigDecimal("100.00"));
                        mockMvc.perform(post("/transferencias")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isOk());

                        // Pequeno delay para garantir diferença de timestamp
                        Thread.sleep(10);
                }

                // Verificar que o histórico está ordenado (mais recente primeiro)
                mockMvc.perform(get("/transferencias/conta/40001"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(5)))
                                .andExpect(jsonPath("$[0].dataHora", notNullValue()))
                                .andExpect(jsonPath("$[4].dataHora", notNullValue()));
        }

        @Test
        void deveRealizarTransferenciaComValorDecimal() throws Exception {
                // Criar clientes
                Cliente origem = new Cliente(null, "Origem Decimal", "50001", new BigDecimal("1000.50"), null);
                Cliente destino = new Cliente(null, "Destino Decimal", "50002", new BigDecimal("500.25"), null);

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(origem)))
                                .andExpect(status().isCreated());

                mockMvc.perform(post("/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(destino)))
                                .andExpect(status().isCreated());

                // Transferir valor com centavos
                TransferenciaRequest request = new TransferenciaRequest("50001", "50002", new BigDecimal("250.75"));

                mockMvc.perform(post("/transferencias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.valor", is(250.75)));

                // Verificar saldos com precisão decimal
                mockMvc.perform(get("/clientes/conta/50001"))
                                .andExpect(jsonPath("$.saldo", is(749.75)));

                mockMvc.perform(get("/clientes/conta/50002"))
                                .andExpect(jsonPath("$.saldo", is(751.00)));
        }
}
