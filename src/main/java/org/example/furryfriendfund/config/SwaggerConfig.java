package org.example.furryfriendfund.config;
import springfox.documentation.service.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;

import io.swagger.v3.oas.models.security.SecurityScheme;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Furry Friend Fund")
                        .description("Furry Friend Fund Documentation")
                        .version("v1.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                )
                .servers(List.of(new Server().url("https://furryfriendfund-gbhdbqchbfaqe7fm.canadacentral-01.azurewebsites.net")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth")) // Add security requirement
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.example.furryfriendfund.controllers")) // Package chứa các API
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("API Documentation")
                .description("API documentation for Spring Boot application")
                .version("1.0")
                .contact(new Contact("Furry Fund","https://fundfe.vercel.app","nghihvdse182563@fpt.edu.vn"))
                .build();
    }

}
