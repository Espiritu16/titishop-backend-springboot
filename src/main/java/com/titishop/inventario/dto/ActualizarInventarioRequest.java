package com.titishop.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "ActualizarInventarioRequest", description = "Payload para actualizar configuración y estado de inventario.")
public record ActualizarInventarioRequest(
		@Schema(description = "Nuevo stock minimo permitido.", example = "18", minimum = "0")
		@NotNull @Min(0) Integer stockMinimo,
		@Schema(description = "Nueva ubicación del inventario.", example = "A1-RACK-04", maxLength = 40)
		@NotBlank @Size(max = 40) String ubicacion,
		@Schema(description = "Estado del inventario.", example = "ACTIVO")
		@NotNull EstadoInventario estado
) {
}
