package com.itau.app.transferencia.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Transferência Bancária")
                        .version("1.0.0")
                        .description("API para gerenciamento de clientes e transferências bancárias.")
                        .contact(new Contact()
                                .name("Time de Desenvolvimento")
                                .email("dev@itau.com.br")));
    }
}
