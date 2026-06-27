package com.titishop.movimientos;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.titishop.compartido.exception.ManejadorGlobalException;
import com.titishop.compartido.response.PaginaResponse;
import com.titishop.movimientos.controller.MovimientoController;
import com.titishop.movimientos.dto.AnularMovimientoRequest;
import com.titishop.movimientos.dto.MovimientoResponse;
import com.titishop.movimientos.dto.RegistrarMovimientoRequest;
import com.titishop.movimientos.dto.TipoMovimiento;
import com.titishop.movimientos.service.MovimientoService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class MovimientoControllerTests {

	private MockMvc mockMvc;
	private MovimientoService movimientoService;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		movimientoService = org.mockito.Mockito.mock(MovimientoService.class);
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();

		mockMvc = MockMvcBuilders.standaloneSetup(new MovimientoController(movimientoService))
				.setControllerAdvice(new ManejadorGlobalException())
				.setValidator(validator)
				.setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
				.build();
	}

	@Test
	void registrarEntradaRetorna201() throws Exception {
		UUID productoId = UUID.randomUUID();
		UUID proveedorId = UUID.randomUUID();
		UUID usuarioId = UUID.randomUUID();
		MovimientoResponse response = movimientoResponse(productoId, proveedorId, usuarioId, TipoMovimiento.ENTRADA);
		when(movimientoService.registrar(any(RegistrarMovimientoRequest.class))).thenReturn(response);

		RegistrarMovimientoRequest request = new RegistrarMovimientoRequest(
				productoId, proveedorId, usuarioId, TipoMovimiento.ENTRADA, 5, null, "Reposicion"
		);

		mockMvc.perform(post("/api/movimientos")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.tipo").value("ENTRADA"))
				.andExpect(jsonPath("$.stockDespues").value(15));
	}

	@Test
	void registrarConPayloadInvalidoRetorna400() throws Exception {
		RegistrarMovimientoRequest request = new RegistrarMovimientoRequest(
				null, null, null, null, 0, null, ""
		);

		mockMvc.perform(post("/api/movimientos")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void registrarConCantidadMayorAlMaximoRetorna400() throws Exception {
		RegistrarMovimientoRequest request = new RegistrarMovimientoRequest(
				UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), TipoMovimiento.ENTRADA, 1001, null, "Reposicion"
		);

		mockMvc.perform(post("/api/movimientos")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.details[0]").value("cantidad: debe ser menor que o igual a 1000"));
	}

	@Test
	void registrarAjusteConStockDestinoMayorAlMaximoRetorna400() throws Exception {
		RegistrarMovimientoRequest request = new RegistrarMovimientoRequest(
				UUID.randomUUID(), null, UUID.randomUUID(), TipoMovimiento.AJUSTE, null, 1001, "Conteo fisico"
		);

		mockMvc.perform(post("/api/movimientos")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.details[0]").value("stockDestino: debe ser menor que o igual a 1000"));
	}

	@Test
	void listarRetornaMovimientos() throws Exception {
		UUID productoId = UUID.randomUUID();
		UUID usuarioId = UUID.randomUUID();
		when(movimientoService.listar(0, 10, null, null, null)).thenReturn(new PaginaResponse<>(
				List.of(movimientoResponse(productoId, null, usuarioId, TipoMovimiento.SALIDA)),
				0,
				10,
				1,
				1,
				true,
				true,
				false
		));

		mockMvc.perform(get("/api/movimientos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].tipo").value("SALIDA"));
	}

	@Test
	void anularRetorna204() throws Exception {
		UUID movimientoId = UUID.randomUUID();
		doNothing().when(movimientoService).anular(any(UUID.class), any(UUID.class), any(String.class));

		AnularMovimientoRequest request = new AnularMovimientoRequest(UUID.randomUUID(), "Registro duplicado");

		mockMvc.perform(post("/api/movimientos/{id}/anulacion", movimientoId)
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isNoContent());
	}

	private MovimientoResponse movimientoResponse(UUID productoId, UUID proveedorId, UUID usuarioId, TipoMovimiento tipo) {
		return new MovimientoResponse(
				UUID.randomUUID(),
				productoId,
				"Mouse",
				"SKU-MOV",
				proveedorId,
				proveedorId == null ? null : "Proveedor Uno",
				tipo,
				5,
				"Reposicion",
				10,
				15,
				usuarioId,
				"Admin Uno",
				Instant.now(),
				null,
				null,
				null
		);
	}
}
