package com.titishop.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "ActualizarEstadoCatalogoRequest", description = "Payload para cambiar solo el estado de una categoría o marca.")
public record ActualizarEstadoCatalogoRequest(
		@Schema(description = "Nuevo estado del registro.", example = "INACTIVO")
		@NotNull EstadoCatalogo estado
) {
}
