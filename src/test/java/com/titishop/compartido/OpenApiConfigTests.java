package com.titishop.compartido;

import static org.assertj.core.api.Assertions.assertThat;

import com.titishop.compartido.config.OpenApiConfig;
import org.junit.jupiter.api.Test;

class OpenApiConfigTests {

	@Test
	void publicaServidorHttpsConfiguradoParaSwagger() {
		var openApi = new OpenApiConfig("https://api-titishop.proyectoutp.com").titishopOpenApi();

		assertThat(openApi.getServers()).hasSize(1);
		assertThat(openApi.getServers().get(0).getUrl()).isEqualTo("https://api-titishop.proyectoutp.com");
	}
}
