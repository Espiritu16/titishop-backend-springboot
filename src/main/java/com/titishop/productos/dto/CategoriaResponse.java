package com.titishop.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "CategoriaResponse", description = "Categoria disponible para clasificar productos.")
public record CategoriaResponse(
		@Schema(description = "Identificador de la categoria.", example = "0f1e2d3c-4b5a-6789-9012-3456789abcde")
		UUID id,
		@Schema(description = "Nombre de la categoria.", example = "Bebidas")
		String nombre,
		@Schema(description = "Estado actual de la categoria.", example = "ACTIVO")
		EstadoCatalogo estado,
		@Schema(description = "Fecha de creacion.", example = "2026-06-01T09:00:00Z")
		Instant creadoEn,
		@Schema(description = "Fecha de actualizacion.", example = "2026-06-02T12:00:00Z")
		Instant actualizadoEn
) {
}
