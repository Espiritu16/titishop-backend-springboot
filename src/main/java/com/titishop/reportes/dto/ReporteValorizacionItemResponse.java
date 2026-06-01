package com.titishop.reportes.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ReporteValorizacionItemResponse(
		UUID productoId,
		String productoNombre,
		String productoSku,
		Integer stockActual,
		BigDecimal precioCompra,
		BigDecimal precioVenta,
		BigDecimal valorCosto,
		BigDecimal valorVenta,
		BigDecimal margenEstimado
) {
}
