package com.titishop.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "ProductoResponse", description = "Producto del catalogo con referencias a categoria y marca.")
public record ProductoResponse(
		@Schema(description = "Identificador del producto.", example = "4f55a1cc-a3f8-4be1-83ab-9b9f4c9c1001")
		UUID id,
		@Schema(description = "Nombre del producto.", example = "Leche Evaporada Entera 410g")
		String nombre,
		@Schema(description = "SKU del producto.", example = "LEC-410-001")
		String sku,
		@Schema(description = "Descripcion del producto.", example = "Leche evaporada entera en lata de 410 gramos.")
		String descripcion,
		@Schema(description = "URL de la imagen del producto.", example = "https://cdn.titishop.local/productos/leche-410g.png", nullable = true)
		String imagenUrl,
		@Schema(description = "Identificador de la categoria.", example = "0f1e2d3c-4b5a-6789-9012-3456789abcde")
		UUID categoriaId,
		@Schema(description = "Nombre de la categoria.", example = "Lacteos")
		String categoriaNombre,
		@Schema(description = "Identificador de la marca.", example = "1ab2cd34-56ef-7890-ab12-cd34ef567890")
		UUID marcaId,
		@Schema(description = "Nombre de la marca.", example = "Gloria")
		String marcaNombre,
		@Schema(description = "Precio de compra vigente.", example = "3.30")
		BigDecimal precioCompra,
		@Schema(description = "Precio de venta vigente.", example = "4.80")
		BigDecimal precioVenta,
		@Schema(description = "Estado del producto.", example = "ACTIVO")
		EstadoProducto estado,
		@Schema(description = "Fecha de creacion.", example = "2026-06-01T08:00:00Z")
		Instant creadoEn,
		@Schema(description = "Fecha de actualizacion.", example = "2026-06-02T11:00:00Z")
		Instant actualizadoEn
) {
}
