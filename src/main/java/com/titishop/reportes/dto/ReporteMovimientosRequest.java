package com.titishop.reportes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.titishop.movimientos.dto.TipoMovimiento;
import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "ReporteMovimientosRequest", description = "Filtros aplicados al reporte de movimientos.")
public record ReporteMovimientosRequest(
		@Schema(description = "Fecha inicial del rango.", example = "2026-06-01", nullable = true)
		LocalDate fechaInicio,
		@Schema(description = "Fecha final del rango.", example = "2026-06-30", nullable = true)
		LocalDate fechaFin,
		@Schema(description = "Filtro por producto.", example = "4f55a1cc-a3f8-4be1-83ab-9b9f4c9c1001", nullable = true)
		UUID productoId,
		@Schema(description = "Filtro por proveedor.", example = "5a81e2d0-55f8-4a3b-8d65-febec9959002", nullable = true)
		UUID proveedorId,
		@Schema(description = "Filtro por tipo de movimiento.", example = "ENTRADA", nullable = true)
		TipoMovimiento tipo,
		@Schema(description = "Indica si se deben incluir movimientos anulados.", example = "false")
		Boolean incluirAnulados
) {
}
