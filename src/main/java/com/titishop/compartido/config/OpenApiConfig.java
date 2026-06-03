package com.titishop.compartido.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI titishopOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("TitiShop API")
						.description("API REST para gestionar autenticacion, usuarios, catalogo, proveedores, inventario, movimientos y reportes de TitiShop.")
						.version("v1")
						.contact(new Contact()
								.name("Equipo TitiShop")
								.email("equipo@titishop.local")))
				.components(new Components()
						.addSecuritySchemes("bearerAuth", new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")));
	}
}
