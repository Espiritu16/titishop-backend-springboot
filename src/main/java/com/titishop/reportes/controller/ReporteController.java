package com.titishop.reportes.controller;

import com.titishop.inventario.dto.EstadoInventario;
import com.titishop.movimientos.dto.TipoMovimiento;
import com.titishop.reportes.dto.ReporteMovimientosRequest;
import com.titishop.reportes.dto.ReporteMovimientosResponse;
import com.titishop.reportes.dto.ReporteStockCriticoResponse;
import com.titishop.reportes.dto.ReporteStockResponse;
import com.titishop.reportes.dto.ReporteValorizacionResponse;
import com.titishop.reportes.service.ReporteService;
import com.titishop.compartido.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Reportes", description = "Consultas analiticas y reportes del sistema.")
@SecurityRequirement(name = "bearerAuth")
public class ReporteController {

	private final ReporteService reporteService;

	public ReporteController(ReporteService reporteService) {
		this.reporteService = reporteService;
	}

	@GetMapping("/movimientos")
	@Operation(summary = "Reporte de movimientos", description = "Genera un reporte filtrable del historial de movimientos.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Reporte generado correctamente.",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReporteMovimientosResponse.class)))),
			@ApiResponse(responseCode = "400", description = "Parámetros de filtro inválidos.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public List<ReporteMovimientosResponse> reporteMovimientos(
			@Parameter(description = "Fecha inicial del rango.", example = "2026-06-01")
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
			@Parameter(description = "Fecha final del rango.", example = "2026-06-30")
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
			@Parameter(description = "Filtra por ID de producto.", example = "4f55a1cc-a3f8-4be1-83ab-9b9f4c9c1001")
			@RequestParam(required = false) UUID productoId,
			@Parameter(description = "Filtra por ID de proveedor.", example = "5a81e2d0-55f8-4a3b-8d65-febec9959002")
			@RequestParam(required = false) UUID proveedorId,
			@Parameter(description = "Filtra por tipo de movimiento.", example = "ENTRADA")
			@RequestParam(required = false) TipoMovimiento tipo,
			@Parameter(description = "Incluye movimientos anulados en el resultado.", example = "false")
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
	@Operation(summary = "Reporte de stock", description = "Genera un reporte del stock actual filtrable por estado, categoría, marca o texto.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Reporte generado correctamente.",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReporteStockResponse.class)))),
			@ApiResponse(responseCode = "400", description = "Parámetros de filtro inválidos.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public List<ReporteStockResponse> reporteStock(
			@Parameter(description = "Filtra por estado de inventario.", example = "ACTIVO")
			@RequestParam(required = false) EstadoInventario estado,
			@Parameter(description = "Filtra por ID de categoría.", example = "0f1e2d3c-4b5a-6789-9012-3456789abcde")
			@RequestParam(required = false) UUID categoriaId,
			@Parameter(description = "Filtra por ID de marca.", example = "1ab2cd34-56ef-7890-ab12-cd34ef567890")
			@RequestParam(required = false) UUID marcaId,
			@Parameter(description = "Texto libre para buscar por nombre o SKU.", example = "leche")
			@RequestParam(required = false) String busqueda
	) {
		return reporteService.reporteStock(estado, categoriaId, marcaId, busqueda);
	}

	@GetMapping("/stock-critico")
	@Operation(summary = "Reporte de stock critico", description = "Lista los productos cuyo stock actual se encuentra en nivel critico.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Reporte generado correctamente.",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReporteStockCriticoResponse.class)))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public List<ReporteStockCriticoResponse> reporteStockCritico() {
		return reporteService.reporteStockCritico();
	}

	@GetMapping("/valorizacion")
	@Operation(summary = "Reporte de valorizacion", description = "Resume el valor del inventario al costo, a venta y el margen estimado.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Reporte generado correctamente.",
					content = @Content(schema = @Schema(implementation = ReporteValorizacionResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ReporteValorizacionResponse reporteValorizacion() {
		return reporteService.reporteValorizacion();
	}
}
