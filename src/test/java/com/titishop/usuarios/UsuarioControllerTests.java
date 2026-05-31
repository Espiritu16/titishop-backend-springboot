package com.titishop.usuarios;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.titishop.compartido.exception.ManejadorGlobalException;
import com.titishop.usuarios.controller.UsuarioController;
import com.titishop.usuarios.dto.ActualizarUsuarioRequest;
import com.titishop.usuarios.dto.CrearUsuarioRequest;
import com.titishop.usuarios.dto.UsuarioResponse;
import com.titishop.usuarios.entity.EstadoUsuario;
import com.titishop.usuarios.entity.RolUsuario;
import com.titishop.usuarios.service.UsuarioService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class UsuarioControllerTests {

	private MockMvc mockMvc;
	private UsuarioService usuarioService;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		usuarioService = org.mockito.Mockito.mock(UsuarioService.class);
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();

		mockMvc = MockMvcBuilders.standaloneSetup(new UsuarioController(usuarioService))
				.setControllerAdvice(new ManejadorGlobalException())
				.setValidator(validator)
				.setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
				.build();
	}

	@Test
	void crearRetorna201() throws Exception {
		CrearUsuarioRequest request = new CrearUsuarioRequest("Admin", "admin@titishop.pe", "password123", RolUsuario.ADMINISTRADOR);
		UsuarioResponse response = new UsuarioResponse(
				UUID.randomUUID(),
				"Admin",
				"admin@titishop.pe",
				RolUsuario.ADMINISTRADOR,
				EstadoUsuario.ACTIVO,
				Instant.now(),
				null
		);

		when(usuarioService.crear(any(CrearUsuarioRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/usuarios")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.email").value("admin@titishop.pe"));
	}

	@Test
	void actualizarConPayloadInvalidoRetorna400() throws Exception {
		ActualizarUsuarioRequest request = new ActualizarUsuarioRequest(
				"",
				"correo-invalido",
				"123",
				RolUsuario.ADMINISTRADOR,
				EstadoUsuario.ACTIVO
		);

		mockMvc.perform(put("/api/usuarios/{id}", UUID.randomUUID())
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void inactivarRetorna204() throws Exception {
		doNothing().when(usuarioService).inactivar(any(UUID.class));

		mockMvc.perform(delete("/api/usuarios/{id}", UUID.randomUUID()))
				.andExpect(status().isNoContent());
	}
}
