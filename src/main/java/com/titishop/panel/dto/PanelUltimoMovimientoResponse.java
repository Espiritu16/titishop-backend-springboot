package com.titishop.panel.dto;

import com.titishop.movimientos.dto.TipoMovimiento;
import java.time.Instant;
import java.util.UUID;

public record PanelUltimoMovimientoResponse(
		UUID id,
		Instant fecha,
		String productoNombre,
		String productoSku,
		TipoMovimiento tipo,
		Integer cantidad,
		String creadoPorNombre
) {
}
