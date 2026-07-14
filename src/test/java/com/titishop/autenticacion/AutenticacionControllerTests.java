package com.titishop.autenticacion;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.titishop.autenticacion.controller.AutenticacionController;
import com.titishop.autenticacion.dto.MensajeResponse;
import com.titishop.autenticacion.dto.ValidarCodigoRecuperacionResponse;
import com.titishop.autenticacion.service.AutenticacionService;
import com.titishop.autenticacion.service.RecuperacionPasswordService;
import com.titishop.compartido.exception.ManejadorGlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class AutenticacionControllerTests {

	private MockMvc mockMvc;
	private RecuperacionPasswordService recuperacionPasswordService;

	@BeforeEach
	void setUp() {
		AutenticacionService autenticacionService = org.mockito.Mockito.mock(AutenticacionService.class);
		recuperacionPasswordService = org.mockito.Mockito.mock(RecuperacionPasswordService.class);
		ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();

		mockMvc = MockMvcBuilders.standaloneSetup(new AutenticacionController(autenticacionService, recuperacionPasswordService))
				.setControllerAdvice(new ManejadorGlobalException())
				.setValidator(validator)
				.setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
				.build();
	}

	@Test
	void solicitarRecuperacionRetornaMensajeGenerico() throws Exception {
		when(recuperacionPasswordService.solicitar(any())).thenReturn(new MensajeResponse("Si el correo existe, se envio un codigo de recuperacion."));

		mockMvc.perform(post("/api/autenticacion/recuperacion/solicitar")
						.contentType("application/json")
						.content("{\"email\":\"admin@titishop.pe\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.mensaje").value("Si el correo existe, se envio un codigo de recuperacion."));
	}

	@Test
	void validarCodigoRetornaResetToken() throws Exception {
		when(recuperacionPasswordService.validar(any())).thenReturn(new ValidarCodigoRecuperacionResponse("reset-token"));

		mockMvc.perform(post("/api/autenticacion/recuperacion/validar")
						.contentType("application/json")
						.content("{\"email\":\"admin@titishop.pe\",\"codigo\":\"123456\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.resetToken").value("reset-token"));
	}

	@Test
	void restablecerPasswordRetornaMensaje() throws Exception {
		when(recuperacionPasswordService.restablecer(any())).thenReturn(new MensajeResponse("Contrasena actualizada correctamente."));

		mockMvc.perform(post("/api/autenticacion/recuperacion/restablecer")
						.contentType("application/json")
						.content("{\"email\":\"admin@titishop.pe\",\"resetToken\":\"reset-token\",\"nuevaPassword\":\"NuevaClave123\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.mensaje").value("Contrasena actualizada correctamente."));
	}
}
