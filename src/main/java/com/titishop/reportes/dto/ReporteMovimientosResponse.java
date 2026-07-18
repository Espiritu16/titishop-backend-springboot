package com.titishop.reportes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.titishop.movimientos.dto.TipoMovimiento;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "ReporteMovimientosResponse", description = "Fila de salida del reporte de movimientos.")
public record ReporteMovimientosResponse(
		@Schema(description = "Identificador del movimiento.", example = "2d11af9c-a8b3-4b63-b89f-2ac3f6852199")
		UUID id,
		@Schema(description = "Fecha del movimiento.", example = "2026-06-02T14:10:00Z")
		Instant fecha,
		@Schema(description = "Producto involucrado.", example = "4f55a1cc-a3f8-4be1-83ab-9b9f4c9c1001")
		UUID productoId,
		@Schema(description = "Nombre del producto.", example = "Leche Evaporada Entera 410g")
		String productoNombre,
		@Schema(description = "SKU del producto.", example = "LEC-410-001")
		String productoSku,
		@Schema(description = "Proveedor vinculado.", example = "5a81e2d0-55f8-4a3b-8d65-febec9959002", nullable = true)
		UUID proveedorId,
		@Schema(description = "Razón social del proveedor.", example = "Distribuidora Lima Norte SAC", nullable = true)
		String proveedorRazonSocial,
		@Schema(description = "Tipo de movimiento.", example = "ENTRADA")
		TipoMovimiento tipo,
		@Schema(description = "Cantidad movilizada.", example = "24")
		Integer cantidad,
		@Schema(description = "Stock antes del movimiento.", example = "96")
		Integer stockAntes,
		@Schema(description = "Stock despues del movimiento.", example = "120")
		Integer stockDespues,
		@Schema(description = "Motivo del movimiento.", example = "Ingreso por compra de reposicion semanal.")
		String motivo,
		@Schema(description = "Usuario que registro el movimiento.", example = "8ddf1f08-6f9d-4d17-9c42-a8b4d6bfc001")
		UUID creadoPorId,
		@Schema(description = "Nombre del usuario que registro el movimiento.", example = "Kevin Espiritu Castillo")
		String creadoPorNombre,
		@Schema(description = "Indica si el movimiento esta anulado.", example = "false")
		Boolean anulado
) {
}
