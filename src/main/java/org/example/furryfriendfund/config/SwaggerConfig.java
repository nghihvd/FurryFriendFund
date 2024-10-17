package org.example.furryfriendfund.config;
import io.swagger.v3.oas.models.servers.Server;

import io.swagger.v3.oas.models.security.SecurityScheme;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI() // contains general information of OpenAPI document
                .info(new Info() // add more information about API
                        .title("Furry Friend Fund") // title api
                        .description("Furry Friend Fund Documentation")
                        .version("v1.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .servers(List.of( new Server().url("http://localhost:8081")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", // corrected spelling here
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP) // corrected spelling here
                                        .scheme("bearer") // corrected spelling here
                                        .bearerFormat("JWT")
                        )
                );
    }

}
