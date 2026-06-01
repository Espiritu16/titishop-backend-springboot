package com.titishop.reportes.dto;

import com.titishop.movimientos.dto.TipoMovimiento;
import java.time.LocalDate;
import java.util.UUID;

public record ReporteMovimientosRequest(
		LocalDate fechaInicio,
		LocalDate fechaFin,
		UUID productoId,
		UUID proveedorId,
		TipoMovimiento tipo,
		Boolean incluirAnulados
) {
}
