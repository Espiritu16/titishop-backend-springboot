package com.titishop.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "CrearCategoriaRequest", description = "Payload para registrar una categoría de productos.")
public record CrearCategoriaRequest(
		@Schema(description = "Nombre único de la categoría.", example = "Bebidas", maxLength = 80)
		@NotBlank @Size(max = 80) String nombre
) {
}
