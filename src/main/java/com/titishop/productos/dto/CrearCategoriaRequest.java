package com.titishop.productos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearCategoriaRequest(
		@NotBlank @Size(max = 80) String nombre
) {
}
