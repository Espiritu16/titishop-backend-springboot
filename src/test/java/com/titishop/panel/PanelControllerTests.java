package com.titishop.panel;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.titishop.compartido.exception.ManejadorGlobalException;
import com.titishop.movimientos.dto.TipoMovimiento;
import com.titishop.panel.controller.PanelController;
import com.titishop.panel.dto.PanelResumenResponse;
import com.titishop.panel.dto.PanelUltimoMovimientoResponse;
import com.titishop.panel.service.PanelService;
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

class PanelControllerTests {

	private MockMvc mockMvc;
	private PanelService panelService;

	@BeforeEach
	void setUp() {
		panelService = org.mockito.Mockito.mock(PanelService.class);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();

		mockMvc = MockMvcBuilders.standaloneSetup(new PanelController(panelService))
				.setControllerAdvice(new ManejadorGlobalException())
				.setValidator(validator)
				.setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
				.build();
	}

	@Test
	void resumenRetornaIndicadoresPanel() throws Exception {
		when(panelService.resumen()).thenReturn(new PanelResumenResponse(
				2,
				1,
				1,
				2,
				1,
				1,
				BigDecimal.valueOf(425).setScale(2),
				List.of(new PanelUltimoMovimientoResponse(
						UUID.randomUUID(),
						Instant.parse("2026-06-01T10:00:00Z"),
						"Mouse",
						"SKU-MOUSE",
						TipoMovimiento.ENTRADA,
						5,
						"Admin Uno"
				))
		));

		mockMvc.perform(get("/api/panel/resumen"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalProductosActivos").value(2))
				.andExpect(jsonPath("$.valorEstimadoInventario").value(425.00))
				.andExpect(jsonPath("$.ultimosMovimientos[0].tipo").value("ENTRADA"));
	}
}
