package com.johanfuertv.movietheaterbackend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Movie Theater API",
        version = "1.0.0",
        description = "REST API for Movie Theater ticket booking system",
        contact = @Contact(
            name = "Movie Theater Team",
            email = "support@movietheater.com"
        )
    )
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class MovieTheaterApplication {
    public static void main(String[] args) {
        SpringApplication.run(MovieTheaterApplication.class, args);
    }
}