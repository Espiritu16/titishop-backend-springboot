package com.titishop.reportes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(name = "ReporteValorizacionItemResponse", description = "Fila del reporte de valorizacion del inventario.")
public record ReporteValorizacionItemResponse(
		@Schema(description = "Identificador del producto.", example = "4f55a1cc-a3f8-4be1-83ab-9b9f4c9c1001")
		UUID productoId,
		@Schema(description = "Nombre del producto.", example = "Leche Evaporada Entera 410g")
		String productoNombre,
		@Schema(description = "SKU del producto.", example = "LEC-410-001")
		String productoSku,
		@Schema(description = "Stock actual.", example = "120")
		Integer stockActual,
		@Schema(description = "Precio de compra.", example = "3.30")
		BigDecimal precioCompra,
		@Schema(description = "Precio de venta.", example = "4.80")
		BigDecimal precioVenta,
		@Schema(description = "Valor total al costo.", example = "396.00")
		BigDecimal valorCosto,
		@Schema(description = "Valor total a venta.", example = "576.00")
		BigDecimal valorVenta,
		@Schema(description = "Margen estimado.", example = "180.00")
		BigDecimal margenEstimado
) {
}
