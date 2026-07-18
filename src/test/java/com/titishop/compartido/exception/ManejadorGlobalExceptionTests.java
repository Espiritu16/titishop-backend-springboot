package com.titishop.compartido.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.titishop.compartido.response.ErrorResponse;
import com.titishop.exportaciones.exception.ExportacionSinDatosException;
import com.titishop.exportaciones.exception.FormatoExportacionInvalidoException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ManejadorGlobalExceptionTests {

	private final ManejadorGlobalException manejador = new ManejadorGlobalException();
	private HttpServletRequest request;

	@BeforeEach
	void setUp() {
		request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("/api/exportaciones/productos/pdf");
	}

	@Test
	void devuelveErrorClaroCuandoFormatoExportacionEsInvalido() {
		var response = manejador.manejarFormatoExportacionInvalido(new FormatoExportacionInvalidoException(), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody())
				.extracting(ErrorResponse::message)
				.isEqualTo("Formato de exportación inválido. Use Excel o PDF.");
		assertThat(response.getBody().details()).containsExactly("formato: valores permitidos excel, pdf");
	}

	@Test
	void devuelveErrorClaroCuandoExportacionNoTieneDatos() {
		var response = manejador.manejarExportacionSinDatos(new ExportacionSinDatosException(), request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		assertThat(response.getBody())
				.extracting(ErrorResponse::message)
				.isEqualTo("No hay datos para exportar con los filtros seleccionados.");
	}

	@Test
	void conservaDetalleDeArgumentosInvalidos() {
		var response = manejador.manejarArgumentoInvalido(
				new IllegalArgumentException("stockEstado debe ser NORMAL, BAJO o AGOTADO."),
				request
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody().details()).containsExactly("stockEstado debe ser NORMAL, BAJO o AGOTADO.");
	}
}
