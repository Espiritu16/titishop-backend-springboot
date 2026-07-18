package com.titishop.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "MarcaResponse", description = "Marca comercial asociada a productos.")
public record MarcaResponse(
		@Schema(description = "Identificador de la marca.", example = "1ab2cd34-56ef-7890-ab12-cd34ef567890")
		UUID id,
		@Schema(description = "Nombre de la marca.", example = "Gloria")
		String nombre,
		@Schema(description = "Estado de la marca.", example = "ACTIVO")
		EstadoCatalogo estado,
		@Schema(description = "Fecha de creación.", example = "2026-06-01T10:00:00Z")
		Instant creadoEn,
		@Schema(description = "Fecha de actualización.", example = "2026-06-02T10:30:00Z")
		Instant actualizadoEn
) {
}
