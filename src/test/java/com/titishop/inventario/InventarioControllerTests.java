package com.titishop.inventario;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.titishop.compartido.exception.ManejadorGlobalException;
import com.titishop.compartido.response.PaginaResponse;
import com.titishop.inventario.controller.InventarioController;
import com.titishop.inventario.dto.CrearInventarioRequest;
import com.titishop.inventario.dto.EstadoInventario;
import com.titishop.inventario.dto.InventarioResponse;
import com.titishop.inventario.service.InventarioService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class InventarioControllerTests {

	private MockMvc mockMvc;
	private InventarioService inventarioService;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		inventarioService = org.mockito.Mockito.mock(InventarioService.class);
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();

		mockMvc = MockMvcBuilders.standaloneSetup(new InventarioController(inventarioService))
				.setControllerAdvice(new ManejadorGlobalException())
				.setValidator(validator)
				.setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
				.build();
	}

	@Test
	void crearRetorna201() throws Exception {
		CrearInventarioRequest request = new CrearInventarioRequest(UUID.randomUUID(), 10, 5, "A-01");
		InventarioResponse response = new InventarioResponse(
				UUID.randomUUID(),
				request.productoId(),
				"Mouse",
				"SKU-1",
				10,
				5,
				"A-01",
				EstadoInventario.ACTIVO,
				false,
				Instant.now(),
				null
		);
		when(inventarioService.crear(any(CrearInventarioRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/inventario")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated());
	}

	@Test
	void crearConPayloadInvalidoRetorna400() throws Exception {
		String body = """
				{
				  "productoId": null,
				  "stockActual": -1,
				  "stockMinimo": -1,
				  "ubicacion": ""
				}
				""";
		mockMvc.perform(post("/api/inventario")
						.contentType("application/json")
						.content(body))
				.andExpect(status().isBadRequest());
	}

	@Test
	void actualizarConPayloadInvalidoRetorna400() throws Exception {
		String body = """
				{
				  "stockMinimo": -1,
				  "ubicacion": "",
				  "estado": null
				}
				""";
		mockMvc.perform(put("/api/inventario/{id}", UUID.randomUUID())
						.contentType("application/json")
						.content(body))
				.andExpect(status().isBadRequest());
	}

	@Test
	void listarAceptaFiltroStockEstado() throws Exception {
		when(inventarioService.listar(0, 10, null, null, "AGOTADO"))
				.thenReturn(new PaginaResponse<>(java.util.List.of(), 0, 10, 0, 0, true, true, true));

		mockMvc.perform(get("/api/inventario").param("stockEstado", "AGOTADO"))
				.andExpect(status().isOk());
	}

	@Test
	void actualizarEstadoConPatchRetorna200() throws Exception {
		UUID id = UUID.randomUUID();
		InventarioResponse response = new InventarioResponse(
				id,
				UUID.randomUUID(),
				"Mouse",
				"SKU-1",
				0,
				5,
				"A-01",
				EstadoInventario.INACTIVO,
				false,
				Instant.now(),
				null
		);
		when(inventarioService.actualizarEstado(any(UUID.class), any())).thenReturn(response);

		mockMvc.perform(patch("/api/inventario/{id}/estado", id)
						.contentType("application/json")
						.content("{\"estado\":\"INACTIVO\"}"))
				.andExpect(status().isOk());
	}
}
