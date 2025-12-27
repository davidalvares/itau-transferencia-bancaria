package com.itau.app.transferencia;

import com.itau.app.transferencia.domain.model.Cliente;
import com.itau.app.transferencia.infrastructure.adapter.in.web.dto.TransferenciaRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
}
