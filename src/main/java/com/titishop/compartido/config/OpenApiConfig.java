package com.titishop.compartido.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	private final String publicUrl;

	public OpenApiConfig(@Value("${app.public-url:https://api-titishop.proyectoutp.com}") String publicUrl) {
		this.publicUrl = publicUrl;
	}

	@Bean
	public OpenAPI titishopOpenApi() {
		return new OpenAPI()
				.servers(List.of(new Server()
						.url(publicUrl)
						.description("Servidor publico de TitiShop")))
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
