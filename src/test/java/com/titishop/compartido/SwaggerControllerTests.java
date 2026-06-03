package com.titishop.compartido;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.titishop.compartido.controller.SwaggerController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class SwaggerControllerTests {

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new SwaggerController()).build();
	}

	@Test
	void swaggerRetornaHtmlSinRedirigir() throws Exception {
		mockMvc.perform(get("/swagger"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/swagger-ui/swagger-ui-bundle.js")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("configUrl: \"/v3/api-docs/swagger-config\"")));
	}
}
