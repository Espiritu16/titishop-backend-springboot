package com.titishop.compartido;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.junit.jupiter.api.Test;

class SwaggerConfiguracionTests {

	@Test
	void springdocQuedaHabilitadoPorDefecto() throws IOException {
		Properties properties = new Properties();
		Path applicationProperties = Path.of("src/main/resources/application.properties");
		try (Reader reader = Files.newBufferedReader(applicationProperties)) {
			properties.load(reader);
		}

		assertThat(properties.getProperty("springdoc.api-docs.enabled")).isEqualTo("${SPRINGDOC_API_DOCS_ENABLED:true}");
		assertThat(properties.getProperty("springdoc.swagger-ui.enabled")).isEqualTo("${SPRINGDOC_SWAGGER_UI_ENABLED:true}");
	}
}
