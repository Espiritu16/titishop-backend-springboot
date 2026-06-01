package com.titishop.reportes.controller;

import com.titishop.inventario.dto.EstadoInventario;
import com.titishop.movimientos.dto.TipoMovimiento;
import com.titishop.reportes.dto.ReporteMovimientosRequest;
import com.titishop.reportes.dto.ReporteMovimientosResponse;
import com.titishop.reportes.dto.ReporteStockCriticoResponse;
import com.titishop.reportes.dto.ReporteStockResponse;
import com.titishop.reportes.dto.ReporteValorizacionResponse;
import com.titishop.reportes.service.ReporteService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

	private final ReporteService reporteService;

	public ReporteController(ReporteService reporteService) {
		this.reporteService = reporteService;
	}

	@GetMapping("/movimientos")
	public List<ReporteMovimientosResponse> reporteMovimientos(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
			@RequestParam(required = false) UUID productoId,
			@RequestParam(required = false) UUID proveedorId,
			@RequestParam(required = false) TipoMovimiento tipo,
			@RequestParam(required = false, defaultValue = "false") Boolean incluirAnulados
	) {
		return reporteService.reporteMovimientos(new ReporteMovimientosRequest(
				fechaInicio,
				fechaFin,
				productoId,
				proveedorId,
				tipo,
				incluirAnulados
		));
	}

	@GetMapping("/stock")
	public List<ReporteStockResponse> reporteStock(
			@RequestParam(required = false) EstadoInventario estado,
			@RequestParam(required = false) UUID categoriaId,
			@RequestParam(required = false) UUID marcaId,
			@RequestParam(required = false) String busqueda
	) {
		return reporteService.reporteStock(estado, categoriaId, marcaId, busqueda);
	}

	@GetMapping("/stock-critico")
	public List<ReporteStockCriticoResponse> reporteStockCritico() {
		return reporteService.reporteStockCritico();
	}

	@GetMapping("/valorizacion")
	public ReporteValorizacionResponse reporteValorizacion() {
		return reporteService.reporteValorizacion();
	}
}
