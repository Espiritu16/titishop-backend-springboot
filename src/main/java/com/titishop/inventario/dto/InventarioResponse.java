package com.titishop.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "InventarioResponse", description = "Inventario consolidado por producto.")
public record InventarioResponse(
		@Schema(description = "Identificador del inventario.", example = "7e11af9c-a8b3-4b63-b89f-2ac3f6852101")
		UUID id,
		@Schema(description = "Identificador del producto.", example = "4f55a1cc-a3f8-4be1-83ab-9b9f4c9c1001")
		UUID productoId,
		@Schema(description = "Nombre del producto.", example = "Leche Evaporada Entera 410g")
		String productoNombre,
		@Schema(description = "SKU del producto.", example = "LEC-410-001")
		String productoSku,
		@Schema(description = "Stock actual.", example = "120")
		Integer stockActual,
		@Schema(description = "Stock minimo configurado.", example = "18")
		Integer stockMinimo,
		@Schema(description = "Ubicación en almacén.", example = "A1-RACK-04")
		String ubicacion,
		@Schema(description = "Estado del inventario.", example = "ACTIVO")
		EstadoInventario estado,
		@Schema(description = "Indica si el producto ya se encuentra en nivel critico.", example = "false")
		boolean stockCritico,
		@Schema(description = "Fecha de creación.", example = "2026-06-01T08:30:00Z")
		Instant creadoEn,
		@Schema(description = "Fecha de actualización.", example = "2026-06-02T16:20:00Z")
		Instant actualizadoEn
) {
}
