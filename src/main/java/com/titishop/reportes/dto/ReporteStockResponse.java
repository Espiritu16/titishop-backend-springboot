package com.titishop.reportes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.titishop.inventario.dto.EstadoInventario;
import java.util.UUID;

@Schema(name = "ReporteStockResponse", description = "Fila del reporte general de stock.")
public record ReporteStockResponse(
		@Schema(description = "Identificador del producto.", example = "4f55a1cc-a3f8-4be1-83ab-9b9f4c9c1001")
		UUID productoId,
		@Schema(description = "Nombre del producto.", example = "Leche Evaporada Entera 410g")
		String productoNombre,
		@Schema(description = "SKU del producto.", example = "LEC-410-001")
		String productoSku,
		@Schema(description = "Identificador de categoría.", example = "0f1e2d3c-4b5a-6789-9012-3456789abcde")
		UUID categoriaId,
		@Schema(description = "Nombre de la categoría.", example = "Lacteos")
		String categoriaNombre,
		@Schema(description = "Identificador de la marca.", example = "1ab2cd34-56ef-7890-ab12-cd34ef567890")
		UUID marcaId,
		@Schema(description = "Nombre de la marca.", example = "Gloria")
		String marcaNombre,
		@Schema(description = "Stock disponible.", example = "120")
		Integer stockActual,
		@Schema(description = "Stock minimo configurado.", example = "18")
		Integer stockMinimo,
		@Schema(description = "Ubicación del inventario.", example = "A1-RACK-04")
		String ubicacion,
		@Schema(description = "Estado del inventario.", example = "ACTIVO")
		EstadoInventario estado,
		@Schema(description = "Indica si se encuentra en stock critico.", example = "false")
		Boolean stockCritico
) {
}
