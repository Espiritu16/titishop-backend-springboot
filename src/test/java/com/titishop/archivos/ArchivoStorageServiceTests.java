package com.titishop.archivos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.titishop.archivos.config.ArchivosProperties;
import com.titishop.archivos.dto.ArchivoResponse;
import com.titishop.archivos.exception.ArchivoInvalidoException;
import com.titishop.archivos.service.ArchivoStorageService;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;

class ArchivoStorageServiceTests {

	@TempDir
	Path tempDir;

	@Test
	void guardarImagenProductoPersisteArchivoYDevuelveUrlPublica() throws Exception {
		ArchivoStorageService service = crearService();
		MockMultipartFile archivo = new MockMultipartFile(
				"archivo",
				"producto.webp",
				"image/webp",
				new byte[] {1, 2, 3, 4}
		);

		ArchivoResponse response = service.guardarImagenProducto(archivo);

		assertThat(response.url()).startsWith("https://api-titishop.proyectoutp.com/uploads/productos/");
		assertThat(response.ruta()).startsWith("productos/");
		assertThat(response.contentType()).isEqualTo("image/webp");
		assertThat(Files.exists(tempDir.resolve(response.ruta()))).isTrue();
	}

	@Test
	void guardarImagenProductoRechazaTipoNoPermitido() {
		ArchivoStorageService service = crearService();
		MockMultipartFile archivo = new MockMultipartFile(
				"archivo",
				"archivo.txt",
				"text/plain",
				"contenido".getBytes()
		);

		assertThatThrownBy(() -> service.guardarImagenProducto(archivo))
				.isInstanceOf(ArchivoInvalidoException.class)
				.hasMessageContaining("Solo se permiten imágenes");
	}

	private ArchivoStorageService crearService() {
		ArchivoStorageService service = new ArchivoStorageService(new ArchivosProperties(
				tempDir,
				"https://api-titishop.proyectoutp.com/uploads",
				DataSize.ofMegabytes(5)
		));
		service.inicializar();
		return service;
	}
}
