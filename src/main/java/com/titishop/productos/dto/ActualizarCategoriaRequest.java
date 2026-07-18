package com.titishop.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "ActualizarCategoriaRequest", description = "Payload para actualizar una categoría existente.")
public record ActualizarCategoriaRequest(
		@Schema(description = "Nombre actualizado de la categoría.", example = "Snacks", maxLength = 80)
		@NotBlank @Size(max = 80) String nombre,
		@Schema(description = "Estado del registro.", example = "ACTIVO")
		@NotNull EstadoCatalogo estado
) {
}
