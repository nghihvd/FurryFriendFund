package org.example.furryfriendfund.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    public OpenAPI customOpenAPI() {
        return new OpenAPI() // contains general information of OpenAPI document
                .info(new Info() // add more information about API
                        .title("Furry Friend Fund") // tittle api
                        .description("Furry Friend Fund Documentation")
                        .version("v1.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation() // external information
                        .description("Full api documentation")
                        .url("http://swagger.io/docs/"));

    }
}
