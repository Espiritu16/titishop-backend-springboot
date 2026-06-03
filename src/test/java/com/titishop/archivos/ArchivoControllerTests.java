package com.titishop.archivos;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.titishop.archivos.controller.ArchivoController;
import com.titishop.archivos.dto.ArchivoResponse;
import com.titishop.archivos.service.ArchivoStorageService;
import com.titishop.compartido.exception.ManejadorGlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ArchivoControllerTests {

	private MockMvc mockMvc;
	private ArchivoStorageService archivoStorageService;

	@BeforeEach
	void setUp() {
		archivoStorageService = org.mockito.Mockito.mock(ArchivoStorageService.class);
		mockMvc = MockMvcBuilders.standaloneSetup(new ArchivoController(archivoStorageService))
				.setControllerAdvice(new ManejadorGlobalException())
				.build();
	}

	@Test
	void subirImagenProductoRetorna201ConUrlPublica() throws Exception {
		MockMultipartFile archivo = new MockMultipartFile(
				"archivo",
				"producto.png",
				"image/png",
				new byte[] {1, 2, 3}
		);
		when(archivoStorageService.guardarImagenProducto(any())).thenReturn(new ArchivoResponse(
				"https://api-titishop.proyectoutp.com/uploads/productos/imagen.png",
				"productos/imagen.png",
				"producto.png",
				"image/png",
				3
		));

		mockMvc.perform(multipart("/api/archivos/productos").file(archivo))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.url").value("https://api-titishop.proyectoutp.com/uploads/productos/imagen.png"));
	}
}
