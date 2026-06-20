package com.titishop.compartido;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:manejador-global;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.hibernate.ddl-auto=none",
		"spring.flyway.enabled=true"
})
@AutoConfigureMockMvc
class ManejadorGlobalExceptionIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void loginConPasswordIncorrectoRetorna401ConMensajeSeguro() throws Exception {
		String body = """
				{
				  "email": "kevin@gmail.com",
				  "password": "password-incorrecto"
				}
				""";

		mockMvc.perform(post("/api/autenticacion/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("Usuario o contrasena incorrectos."));
	}

	@Test
	void loginConUsuarioInexistenteRetorna401ConMensajeSeguro() throws Exception {
		String body = """
				{
				  "email": "noexiste@gmail.com",
				  "password": "password123"
				}
				""";

		mockMvc.perform(post("/api/autenticacion/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("Usuario o contrasena incorrectos."));
	}

	@Test
	void parametroPaginacionInvalidoRetorna400() throws Exception {
		mockMvc.perform(get("/api/productos")
						.param("size", "0")
						.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ALMACENERO"))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Parametro de solicitud invalido."));
	}

	@Test
	void paginaNegativaRetorna400() throws Exception {
		mockMvc.perform(get("/api/productos")
						.param("page", "-1")
						.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ALMACENERO"))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Parametro de solicitud invalido."));
	}

	@Test
	void tamanoPaginaExcesivoRetorna400() throws Exception {
		mockMvc.perform(get("/api/productos")
						.param("size", "101")
						.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ALMACENERO"))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Parametro de solicitud invalido."));
	}

	@Test
	void parametroEnumInvalidoRetorna400() throws Exception {
		mockMvc.perform(get("/api/productos")
						.param("estado", "NO_EXISTE")
						.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ALMACENERO"))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Parametro de solicitud invalido."));
	}

	@Test
	void stockEstadoInvalidoRetorna400() throws Exception {
		mockMvc.perform(get("/api/inventario")
						.param("stockEstado", "INVALIDO")
						.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ALMACENERO"))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Parametro de solicitud invalido."));
	}
}
