package com.titishop.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "ActualizarEstadoProductoRequest", description = "Payload para cambiar solo el estado de un producto.")
public record ActualizarEstadoProductoRequest(
		@Schema(description = "Nuevo estado del producto.", example = "INACTIVO")
		@NotNull EstadoProducto estado
) {
}
