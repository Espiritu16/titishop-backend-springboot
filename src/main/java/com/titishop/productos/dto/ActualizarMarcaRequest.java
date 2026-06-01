package com.titishop.productos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActualizarMarcaRequest(
		@NotBlank @Size(max = 80) String nombre,
		@NotNull EstadoCatalogo estado
) {
}
