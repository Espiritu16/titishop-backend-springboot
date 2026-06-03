package com.titishop.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "CrearMarcaRequest", description = "Payload para registrar una marca comercial.")
public record CrearMarcaRequest(
		@Schema(description = "Nombre unico de la marca.", example = "Coca-Cola", maxLength = 80)
		@NotBlank @Size(max = 80) String nombre
) {
}
