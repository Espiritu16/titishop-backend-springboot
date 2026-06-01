package com.titishop.panel.dto;

import java.math.BigDecimal;
import java.util.List;

public record PanelResumenResponse(
		Integer totalProductosActivos,
		Integer totalProveedoresActivos,
		Integer productosStockCritico,
		Integer movimientosDelDia,
		Integer entradasDelMes,
		Integer salidasDelMes,
		BigDecimal valorEstimadoInventario,
		List<PanelUltimoMovimientoResponse> ultimosMovimientos
) {
}
