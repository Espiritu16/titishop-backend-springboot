package com.titishop.compartido.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(name = "ErrorResponse", description = "Estructura estandar de error devuelta por la API.")
public record ErrorResponse(
		@Schema(description = "Fecha y hora en la que ocurrio el error.", example = "2026-06-02T21:45:00Z")
		Instant timestamp,
		@Schema(description = "Codigo HTTP de la respuesta.", example = "400")
		int status,
		@Schema(description = "Descripcion corta del estado HTTP.", example = "Bad Request")
		String error,
		@Schema(description = "Mensaje principal del error.", example = "Error de validacion.")
		String message,
		@Schema(description = "Ruta solicitada que produjo el error.", example = "/api/productos")
		String path,
		@ArraySchema(schema = @Schema(description = "Detalle adicional del error.", example = "nombre: no debe estar vacio"))
		List<String> details
) {
}
