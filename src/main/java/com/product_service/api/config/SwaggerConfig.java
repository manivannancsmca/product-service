package com.product_service.api.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI productServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Service API")
                        .description("Product microservice endpoints")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Your Name")
                                .email("your@email.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Product Service Wiki")
                        .url("https://github.com"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8200")        //  Gateway URL
                                .description("API Gateway")));
    }
}
