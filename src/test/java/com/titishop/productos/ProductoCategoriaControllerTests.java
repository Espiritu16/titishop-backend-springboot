package com.titishop.productos;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.titishop.compartido.exception.ManejadorGlobalException;
import com.titishop.productos.controller.CategoriaController;
import com.titishop.productos.controller.MarcaController;
import com.titishop.productos.controller.ProductoController;
import com.titishop.productos.dto.CrearCategoriaRequest;
import com.titishop.productos.dto.CrearMarcaRequest;
import com.titishop.productos.dto.CrearProductoRequest;
import com.titishop.productos.dto.EstadoProducto;
import com.titishop.productos.dto.ProductoResponse;
import com.titishop.productos.service.CategoriaService;
import com.titishop.productos.service.MarcaService;
import com.titishop.productos.service.ProductoService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class ProductoCategoriaControllerTests {

	private MockMvc mockMvc;
	private ProductoService productoService;
	private CategoriaService categoriaService;
	private MarcaService marcaService;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		productoService = org.mockito.Mockito.mock(ProductoService.class);
		categoriaService = org.mockito.Mockito.mock(CategoriaService.class);
		marcaService = org.mockito.Mockito.mock(MarcaService.class);
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();

		mockMvc = MockMvcBuilders.standaloneSetup(
						new ProductoController(productoService),
						new CategoriaController(categoriaService),
						new MarcaController(marcaService))
				.setControllerAdvice(new ManejadorGlobalException())
				.setValidator(validator)
				.setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
				.build();
	}

	@Test
	void crearProductoRetorna201() throws Exception {
		CrearProductoRequest request = new CrearProductoRequest(
				"Mouse", "SKU-1", "Optico", null, UUID.randomUUID(), UUID.randomUUID(), BigDecimal.ONE, BigDecimal.TEN
		);
		ProductoResponse response = new ProductoResponse(
				UUID.randomUUID(),
				"Mouse",
				"SKU-1",
				"Optico",
				null,
				request.categoriaId(),
				"Perifericos",
				request.marcaId(),
				"Logi",
				BigDecimal.ONE,
				BigDecimal.TEN,
				EstadoProducto.ACTIVO,
				Instant.now(),
				null
		);

		when(productoService.crear(any(CrearProductoRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/productos")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated());
	}

	@Test
	void crearCategoriaInvalidaRetorna400() throws Exception {
		CrearCategoriaRequest request = new CrearCategoriaRequest("");
		mockMvc.perform(post("/api/categorias")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void crearMarcaInvalidaRetorna400() throws Exception {
		CrearMarcaRequest request = new CrearMarcaRequest("");
		mockMvc.perform(post("/api/marcas")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void actualizarProductoInvalidoRetorna400() throws Exception {
		String body = """
				{
				  "nombre": "",
				  "sku": "",
				  "descripcion": "",
				  "categoriaId": null,
				  "marcaId": null,
				  "precioCompra": -1,
				  "precioVenta": -2,
				  "estado": null
				}
				""";
		mockMvc.perform(put("/api/productos/{id}", UUID.randomUUID())
						.contentType("application/json")
						.content(body))
				.andExpect(status().isBadRequest());
	}

	@Test
	void actualizarEstadoProductoConPatchRetorna200() throws Exception {
		UUID id = UUID.randomUUID();
		ProductoResponse response = new ProductoResponse(
				id,
				"Mouse",
				"SKU-1",
				"Optico",
				null,
				UUID.randomUUID(),
				"Perifericos",
				UUID.randomUUID(),
				"Logi",
				BigDecimal.ONE,
				BigDecimal.TEN,
				EstadoProducto.INACTIVO,
				Instant.now(),
				null
		);
		when(productoService.actualizarEstado(any(UUID.class), any())).thenReturn(response);

		mockMvc.perform(patch("/api/productos/{id}/estado", id)
						.contentType("application/json")
						.content("""
								{ "estado": "INACTIVO" }
								"""))
				.andExpect(status().isOk());
	}

	@Test
	void actualizarEstadoProductoSinEstadoRetorna400() throws Exception {
		mockMvc.perform(patch("/api/productos/{id}/estado", UUID.randomUUID())
						.contentType("application/json")
						.content("{}"))
				.andExpect(status().isBadRequest());
	}
}
