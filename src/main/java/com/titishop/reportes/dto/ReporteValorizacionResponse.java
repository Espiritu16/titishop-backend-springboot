package com.titishop.reportes.dto;

import java.math.BigDecimal;
import java.util.List;

public record ReporteValorizacionResponse(
		List<ReporteValorizacionItemResponse> items,
		BigDecimal valorCostoTotal,
		BigDecimal valorVentaTotal,
		BigDecimal margenEstimadoTotal
) {
}
