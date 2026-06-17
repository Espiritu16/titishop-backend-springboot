package com.titishop.compartido.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "PaginaResponse", description = "Respuesta estandar para listados paginados.")
public record PaginaResponse<T>(
		@Schema(description = "Registros de la pagina solicitada.")
		List<T> content,
		@Schema(description = "Indice de pagina actual basado en cero.", example = "0")
		int page,
		@Schema(description = "Cantidad de registros por pagina.", example = "10")
		int size,
		@Schema(description = "Cantidad total de registros encontrados.", example = "48")
		long totalElements,
		@Schema(description = "Cantidad total de paginas disponibles.", example = "5")
		int totalPages,
		@Schema(description = "Indica si es la primera pagina.", example = "true")
		boolean first,
		@Schema(description = "Indica si es la ultima pagina.", example = "false")
		boolean last,
		@Schema(description = "Indica si no hay registros en la pagina actual.", example = "false")
		boolean empty
) {
}
