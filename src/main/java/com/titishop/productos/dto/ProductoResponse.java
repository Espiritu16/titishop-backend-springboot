package com.titishop.productos.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductoResponse(
		UUID id,
		String nombre,
		String sku,
		String descripcion,
		String imagenUrl,
		UUID categoriaId,
		String categoriaNombre,
		UUID marcaId,
		String marcaNombre,
		BigDecimal precioCompra,
		BigDecimal precioVenta,
		EstadoProducto estado,
		Instant creadoEn,
		Instant actualizadoEn
) {
}
