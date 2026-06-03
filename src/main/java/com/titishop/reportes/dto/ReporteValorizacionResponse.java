package com.titishop.reportes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(name = "ReporteValorizacionResponse", description = "Resumen de valorizacion del inventario.")
public record ReporteValorizacionResponse(
		@Schema(description = "Detalle por producto.")
		List<ReporteValorizacionItemResponse> items,
		@Schema(description = "Valor total al costo del inventario.", example = "18250.40")
		BigDecimal valorCostoTotal,
		@Schema(description = "Valor total potencial de venta.", example = "24790.90")
		BigDecimal valorVentaTotal,
		@Schema(description = "Margen total estimado.", example = "6540.50")
		BigDecimal margenEstimadoTotal
) {
}
