package com.titishop.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActualizarInventarioRequest(
		@NotNull @Min(0) Integer stockMinimo,
		@NotBlank @Size(max = 40) String ubicacion,
		@NotNull EstadoInventario estado
) {
}
