package com.titishop.reportes.dto;

import com.titishop.movimientos.dto.TipoMovimiento;
import java.time.Instant;
import java.util.UUID;

public record ReporteMovimientosResponse(
		UUID id,
		Instant fecha,
		UUID productoId,
		String productoNombre,
		String productoSku,
		UUID proveedorId,
		String proveedorRazonSocial,
		TipoMovimiento tipo,
		Integer cantidad,
		Integer stockAntes,
		Integer stockDespues,
		String motivo,
		UUID creadoPorId,
		String creadoPorNombre,
		Boolean anulado
) {
}
