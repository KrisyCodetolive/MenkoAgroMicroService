package com.menkoagro.api.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI menkoAgroOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Menko Agro API")
                        .description("""
                                API de gestion de l'entreprise agricole **Menko Agro**.

                                **Modules** : Auth · Stock · Produits · Catalogue · Production · Clients · Ventes · Rapport

                                **Authentification** : Utilisez `POST /auth/login` pour obtenir un token JWT, \
                                puis cliquez sur **Authorize** et entrez `<votre-token>` (sans le préfixe Bearer).
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Menko Agro")
                                .email("contact@menkoagro.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080/api/v1").description("Développement local"),
                        new Server().url("https://api.menkoagro.com/api/v1").description("Production")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT obtenu via `POST /auth/login`. " +
                                                "Collez uniquement le token (sans le mot 'Bearer').")));
    }
}
