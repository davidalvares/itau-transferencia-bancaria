package com.itau.app.transferencia;

import com.itau.app.transferencia.domain.model.Cliente;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
