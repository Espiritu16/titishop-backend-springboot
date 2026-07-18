package com.titishop.compartido.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "PaginaResponse", description = "Respuesta estándar para listados paginados.")
public record PaginaResponse<T>(
		@Schema(description = "Registros de la página solicitada.")
		List<T> content,
		@Schema(description = "Índice de página actual basado en cero.", example = "0")
		int page,
		@Schema(description = "Cantidad de registros por página.", example = "10")
		int size,
		@Schema(description = "Cantidad total de registros encontrados.", example = "48")
		long totalElements,
		@Schema(description = "Cantidad total de páginas disponibles.", example = "5")
		int totalPages,
		@Schema(description = "Indica si es la primera página.", example = "true")
		boolean first,
		@Schema(description = "Indica si es la última página.", example = "false")
		boolean last,
		@Schema(description = "Indica si no hay registros en la página actual.", example = "false")
		boolean empty
) {
}
