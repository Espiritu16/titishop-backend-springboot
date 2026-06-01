package com.titishop.productos.dto;

import java.time.Instant;
import java.util.UUID;

public record MarcaResponse(
		UUID id,
		String nombre,
		EstadoCatalogo estado,
		Instant creadoEn,
		Instant actualizadoEn
) {
}
