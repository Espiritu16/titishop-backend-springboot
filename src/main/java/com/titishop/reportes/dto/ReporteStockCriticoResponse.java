package com.titishop.reportes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(name = "ReporteStockCriticoResponse", description = "Fila del reporte de productos en stock critico.")
public record ReporteStockCriticoResponse(
		@Schema(description = "Identificador del producto.", example = "4f55a1cc-a3f8-4be1-83ab-9b9f4c9c1001")
		UUID productoId,
		@Schema(description = "Nombre del producto.", example = "Leche Evaporada Entera 410g")
		String productoNombre,
		@Schema(description = "SKU del producto.", example = "LEC-410-001")
		String productoSku,
		@Schema(description = "Stock actual.", example = "8")
		Integer stockActual,
		@Schema(description = "Stock minimo configurado.", example = "18")
		Integer stockMinimo,
		@Schema(description = "Cantidad sugerida para reposicion.", example = "10")
		Integer cantidadSugerida,
		@Schema(description = "Ubicación del inventario.", example = "A1-RACK-04")
		String ubicacion
) {
}
