package com.titishop.reportes;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.titishop.compartido.exception.ManejadorGlobalException;
import com.titishop.inventario.dto.EstadoInventario;
import com.titishop.movimientos.dto.TipoMovimiento;
import com.titishop.reportes.controller.ReporteController;
import com.titishop.reportes.dto.ReporteMovimientosRequest;
import com.titishop.reportes.dto.ReporteMovimientosResponse;
import com.titishop.reportes.dto.ReporteStockCriticoResponse;
import com.titishop.reportes.dto.ReporteStockResponse;
import com.titishop.reportes.dto.ReporteValorizacionResponse;
import com.titishop.reportes.service.ReporteService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class ReporteControllerTests {

	private MockMvc mockMvc;
	private ReporteService reporteService;

	@BeforeEach
	void setUp() {
		reporteService = org.mockito.Mockito.mock(ReporteService.class);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();

		mockMvc = MockMvcBuilders.standaloneSetup(new ReporteController(reporteService))
				.setControllerAdvice(new ManejadorGlobalException())
				.setValidator(validator)
				.setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
				.build();
	}

	@Test
	void reporteMovimientosRetornaFiltroAplicado() throws Exception {
		UUID productoId = UUID.randomUUID();
		ReporteMovimientosResponse response = new ReporteMovimientosResponse(
				UUID.randomUUID(),
				Instant.parse("2026-06-01T10:00:00Z"),
				productoId,
				"Mouse",
				"SKU-MOUSE",
				null,
				null,
				TipoMovimiento.SALIDA,
				2,
				10,
				8,
				"Venta",
				UUID.randomUUID(),
				"Admin Uno",
				false
		);
		when(reporteService.reporteMovimientos(any(ReporteMovimientosRequest.class))).thenReturn(List.of(response));

		mockMvc.perform(get("/api/reportes/movimientos")
						.param("tipo", "SALIDA")
						.param("fechaInicio", "2026-06-01")
						.param("fechaFin", "2026-06-01"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].tipo").value("SALIDA"))
				.andExpect(jsonPath("$[0].stockDespues").value(8));
	}

	@Test
	void reporteStockRetornaInventario() throws Exception {
		UUID productoId = UUID.randomUUID();
		when(reporteService.reporteStock(eq(null), eq(null), eq(null), eq("mouse"))).thenReturn(List.of(
				new ReporteStockResponse(productoId, "Mouse", "SKU-MOUSE", UUID.randomUUID(), "Perifericos",
						UUID.randomUUID(), "Logi", 8, 5, "A-01", EstadoInventario.ACTIVO, false)
		));

		mockMvc.perform(get("/api/reportes/stock").param("busqueda", "mouse"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].productoSku").value("SKU-MOUSE"));
	}

	@Test
	void reporteStockCriticoRetornaCantidadSugerida() throws Exception {
		when(reporteService.reporteStockCritico()).thenReturn(List.of(
				new ReporteStockCriticoResponse(UUID.randomUUID(), "Mouse", "SKU-MOUSE", 3, 5, 2, "A-01")
		));

		mockMvc.perform(get("/api/reportes/stock-critico"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].cantidadSugerida").value(2));
	}

	@Test
	void reporteValorizacionRetornaTotales() throws Exception {
		when(reporteService.reporteValorizacion()).thenReturn(new ReporteValorizacionResponse(
				List.of(), BigDecimal.valueOf(190).setScale(2), BigDecimal.valueOf(310).setScale(2), BigDecimal.valueOf(120).setScale(2)
		));

		mockMvc.perform(get("/api/reportes/valorizacion"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.valorCostoTotal").value(190.00))
				.andExpect(jsonPath("$.margenEstimadoTotal").value(120.00));
	}
}
