package com.titishop.panel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(name = "PanelResumenResponse", description = "Resumen general del panel principal.")
public record PanelResumenResponse(
		@Schema(description = "Cantidad de productos activos.", example = "128")
		Integer totalProductosActivos,
		@Schema(description = "Cantidad de proveedores activos.", example = "17")
		Integer totalProveedoresActivos,
		@Schema(description = "Cantidad de productos en stock critico.", example = "6")
		Integer productosStockCritico,
		@Schema(description = "Cantidad de movimientos del dia.", example = "14")
		Integer movimientosDelDia,
		@Schema(description = "Cantidad de entradas registradas en el mes.", example = "42")
		Integer entradasDelMes,
		@Schema(description = "Cantidad de salidas registradas en el mes.", example = "31")
		Integer salidasDelMes,
		@Schema(description = "Valor estimado del inventario al costo.", example = "18250.40")
		BigDecimal valorEstimadoInventario,
		@Schema(description = "Ultimos movimientos registrados.")
		List<PanelUltimoMovimientoResponse> ultimosMovimientos
) {
}
