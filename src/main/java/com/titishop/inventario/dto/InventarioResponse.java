package com.titishop.inventario.dto;

import java.time.Instant;
import java.util.UUID;

public record InventarioResponse(
		UUID id,
		UUID productoId,
		String productoNombre,
		String productoSku,
		Integer stockActual,
		Integer stockMinimo,
		String ubicacion,
		EstadoInventario estado,
		boolean stockCritico,
		Instant creadoEn,
		Instant actualizadoEn
) {
}
