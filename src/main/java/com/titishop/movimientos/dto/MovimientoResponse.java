package com.titishop.movimientos.dto;

import java.time.Instant;
import java.util.UUID;

public record MovimientoResponse(
		UUID id,
		UUID productoId,
		String productoNombre,
		String productoSku,
		UUID proveedorId,
		String proveedorRazonSocial,
		TipoMovimiento tipo,
		Integer cantidad,
		String motivo,
		Integer stockAntes,
		Integer stockDespues,
		UUID creadoPorId,
		String creadoPorNombre,
		Instant creadoEn,
		Instant anuladoEn,
		UUID anuladoPorId,
		String motivoAnulacion
) {
}
