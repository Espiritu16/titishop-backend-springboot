package com.titishop.reportes.dto;

import java.util.UUID;

public record ReporteStockCriticoResponse(
		UUID productoId,
		String productoNombre,
		String productoSku,
		Integer stockActual,
		Integer stockMinimo,
		Integer cantidadSugerida,
		String ubicacion
) {
}
