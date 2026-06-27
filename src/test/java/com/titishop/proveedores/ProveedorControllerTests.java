package com.titishop.proveedores;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.titishop.compartido.exception.ManejadorGlobalException;
import com.titishop.proveedores.controller.ProveedorController;
import com.titishop.proveedores.dto.ActualizarProveedorRequest;
import com.titishop.proveedores.dto.ConsultaRucProveedorResponse;
import com.titishop.proveedores.dto.CrearProveedorRequest;
import com.titishop.proveedores.dto.EstadoProveedor;
import com.titishop.proveedores.dto.ProveedorResponse;
import com.titishop.proveedores.service.ProveedorService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class ProveedorControllerTests {

	private MockMvc mockMvc;
	private ProveedorService proveedorService;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		proveedorService = org.mockito.Mockito.mock(ProveedorService.class);
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();

		mockMvc = MockMvcBuilders.standaloneSetup(new ProveedorController(proveedorService))
				.setControllerAdvice(new ManejadorGlobalException())
				.setValidator(validator)
				.setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
				.build();
	}

	@Test
	void crearRetorna201() throws Exception {
		CrearProveedorRequest request = crearRequest();
		ProveedorResponse response = new ProveedorResponse(
				UUID.randomUUID(),
				"Proveedor Uno",
				"20609998881",
				"987654321",
				"014700000",
				"ventas@uno.pe",
				"Av. Uno 123",
				EstadoProveedor.ACTIVO,
				Instant.now(),
				null
		);
		when(proveedorService.crear(any(CrearProveedorRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/proveedores")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.ruc").value("20609998881"));
	}

	@Test
	void actualizarConPayloadInvalidoRetorna400() throws Exception {
		ActualizarProveedorRequest request = new ActualizarProveedorRequest(
				"",
				"123",
				"987",
				"014",
				"correo-invalido",
				"",
				EstadoProveedor.ACTIVO
		);

		mockMvc.perform(put("/api/proveedores/{id}", UUID.randomUUID())
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void consultarRucRetornaDatosFactiliza() throws Exception {
		ConsultaRucProveedorResponse response = new ConsultaRucProveedorResponse(
				"20609998881",
				"FACTILIZA S.A.C.",
				"AV. LOS OLIVOS 123",
				"AV. LOS OLIVOS 123 LIMA LIMA MIRAFLORES",
				"LIMA",
				"LIMA",
				"MIRAFLORES",
				"ACTIVO",
				"HABIDO"
		);
		when(proveedorService.consultarRuc("20609998881")).thenReturn(response);

		mockMvc.perform(get("/api/proveedores/consulta-ruc/{ruc}", "20609998881"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.razonSocial").value("FACTILIZA S.A.C."));
	}

	@Test
	void inactivarRetorna204() throws Exception {
		doNothing().when(proveedorService).inactivar(any(UUID.class));

		mockMvc.perform(delete("/api/proveedores/{id}", UUID.randomUUID()))
				.andExpect(status().isNoContent());
	}

	@Test
	void actualizarEstadoConPatchRetorna200() throws Exception {
		UUID id = UUID.randomUUID();
		ProveedorResponse response = new ProveedorResponse(
				id,
				"Proveedor Uno",
				"20609998881",
				"987654321",
				"014700000",
				"ventas@uno.pe",
				"Av. Uno 123",
				EstadoProveedor.INACTIVO,
				Instant.now(),
				null
		);
		when(proveedorService.actualizarEstado(any(UUID.class), any())).thenReturn(response);

		mockMvc.perform(patch("/api/proveedores/{id}/estado", id)
						.contentType("application/json")
						.content("{\"estado\":\"INACTIVO\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("INACTIVO"));
	}

	private CrearProveedorRequest crearRequest() {
		return new CrearProveedorRequest(
				"Proveedor Uno",
				"20609998881",
				"987654321",
				"014700000",
				"ventas@uno.pe",
				"Av. Uno 123"
		);
	}
}
