package com.titishop.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "ActualizarMarcaRequest", description = "Payload para actualizar una marca registrada.")
public record ActualizarMarcaRequest(
		@Schema(description = "Nombre actualizado de la marca.", example = "Gloria", maxLength = 80)
		@NotBlank @Size(max = 80) String nombre,
		@Schema(description = "Estado actual de la marca.", example = "ACTIVO")
		@NotNull EstadoCatalogo estado
) {
}
