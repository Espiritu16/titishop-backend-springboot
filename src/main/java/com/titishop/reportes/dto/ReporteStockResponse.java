package com.titishop.reportes.dto;

import com.titishop.inventario.dto.EstadoInventario;
import java.util.UUID;

public record ReporteStockResponse(
		UUID productoId,
		String productoNombre,
		String productoSku,
		UUID categoriaId,
		String categoriaNombre,
		UUID marcaId,
		String marcaNombre,
		Integer stockActual,
		Integer stockMinimo,
		String ubicacion,
		EstadoInventario estado,
		Boolean stockCritico
) {
}
