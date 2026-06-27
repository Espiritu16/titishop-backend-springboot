package com.titishop.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "ActualizarEstadoInventarioRequest", description = "Payload para cambiar solo el estado de un inventario.")
public record ActualizarEstadoInventarioRequest(
		@Schema(description = "Nuevo estado del inventario.", example = "INACTIVO")
		@NotNull EstadoInventario estado
) {
}
