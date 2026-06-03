package com.titishop.archivos.controller;

import com.titishop.archivos.dto.ArchivoResponse;
import com.titishop.archivos.service.ArchivoStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/archivos")
@Tag(name = "Archivos", description = "Carga de imagenes locales para recursos del sistema.")
@SecurityRequirement(name = "bearerAuth")
public class ArchivoController {

	private final ArchivoStorageService archivoStorageService;

	public ArchivoController(ArchivoStorageService archivoStorageService) {
		this.archivoStorageService = archivoStorageService;
	}

	@PostMapping(value = "/productos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(
			summary = "Subir imagen de producto",
			description = "Guarda una imagen en el almacenamiento local del VPS y devuelve la URL publica.",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					required = true,
					content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
			)
	)
	public ArchivoResponse subirImagenProducto(
			@Schema(description = "Imagen JPG, PNG, WEBP o GIF.", type = "string", format = "binary")
			@RequestParam("archivo") MultipartFile archivo
	) {
		return archivoStorageService.guardarImagenProducto(archivo);
	}
}
