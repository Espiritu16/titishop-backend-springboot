package com.titishop.movimientos.controller;

import com.titishop.movimientos.dto.AnularMovimientoRequest;
import com.titishop.movimientos.dto.MovimientoResponse;
import com.titishop.movimientos.dto.RegistrarMovimientoRequest;
import com.titishop.movimientos.dto.TipoMovimiento;
import com.titishop.movimientos.service.MovimientoService;
import com.titishop.compartido.response.ErrorResponse;
import com.titishop.compartido.response.PaginaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/movimientos")
@Tag(name = "Movimientos", description = "Registro y consulta de movimientos de inventario.")
@SecurityRequirement(name = "bearerAuth")
public class MovimientoController {

	private final MovimientoService movimientoService;

	public MovimientoController(MovimientoService movimientoService) {
		this.movimientoService = movimientoService;
	}

	@GetMapping
	@Operation(summary = "Listar movimientos", description = "Obtiene el historial completo de movimientos de inventario.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Listado obtenido correctamente.",
					content = @Content(schema = @Schema(implementation = PaginaResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public PaginaResponse<MovimientoResponse> listar(
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
			@RequestParam(required = false) String busqueda,
			@RequestParam(required = false) TipoMovimiento tipo,
			@RequestParam(required = false) Boolean anulado,
			@RequestParam(required = false) UUID productoId
	) {
		return movimientoService.listar(page, size, busqueda, tipo, anulado, productoId);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener movimiento por ID", description = "Busca un movimiento especifico por su identificador.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Movimiento encontrado.",
					content = @Content(schema = @Schema(implementation = MovimientoResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Movimiento no encontrado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public MovimientoResponse obtenerPorId(@Parameter(description = "ID del movimiento.", example = "2d11af9c-a8b3-4b63-b89f-2ac3f6852199") @PathVariable UUID id) {
		return movimientoService.obtenerPorId(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Registrar movimiento", description = "Registra una entrada, salida o ajuste sobre el inventario de un producto.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			description = "Datos del movimiento a registrar.",
			content = @Content(
					schema = @Schema(implementation = RegistrarMovimientoRequest.class),
					examples = {
							@ExampleObject(
									name = "Entrada",
									value = """
											{
											  "productoId": "4f55a1cc-a3f8-4be1-83ab-9b9f4c9c1001",
											  "proveedorId": "5a81e2d0-55f8-4a3b-8d65-febec9959002",
											  "usuarioId": "8ddf1f08-6f9d-4d17-9c42-a8b4d6bfc001",
											  "tipo": "ENTRADA",
											  "cantidad": 24,
											  "stockDestino": null,
											  "motivo": "Ingreso por compra de reposicion semanal."
											}
											"""
							),
							@ExampleObject(
									name = "Ajuste",
									value = """
											{
											  "productoId": "4f55a1cc-a3f8-4be1-83ab-9b9f4c9c1001",
											  "proveedorId": null,
											  "usuarioId": "8ddf1f08-6f9d-4d17-9c42-a8b4d6bfc001",
											  "tipo": "AJUSTE",
											  "cantidad": 1,
											  "stockDestino": 140,
											  "motivo": "Ajuste por conteo fisico."
											}
											"""
							)
					}
			)
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Movimiento registrado correctamente.",
					content = @Content(schema = @Schema(implementation = MovimientoResponse.class))),
			@ApiResponse(responseCode = "400", description = "Datos de entrada inválidos.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Producto, proveedor o usuario no encontrado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "422", description = "Regla de negocio invalida para el movimiento.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public MovimientoResponse registrar(@Valid @RequestBody RegistrarMovimientoRequest request) {
		return movimientoService.registrar(request);
	}

	@PostMapping("/{id}/anulacion")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Anular movimiento", description = "Anula un movimiento existente registrando el motivo y el usuario responsable.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			description = "Datos requeridos para anular el movimiento.",
			content = @Content(
					schema = @Schema(implementation = AnularMovimientoRequest.class),
					examples = @ExampleObject(
							name = "Anulacion",
							value = """
									{
									  "usuarioId": "8ddf1f08-6f9d-4d17-9c42-a8b4d6bfc001",
									  "motivoAnulacion": "Registro duplicado por error de digitacion."
									}
									"""
					)
			)
	)
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Movimiento anulado correctamente."),
			@ApiResponse(responseCode = "400", description = "Datos de entrada inválidos.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Movimiento o usuario no encontrado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "422", description = "El movimiento no puede anularse por regla de negocio.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public void anular(
			@Parameter(description = "ID del movimiento a anular.", example = "2d11af9c-a8b3-4b63-b89f-2ac3f6852199")
			@PathVariable UUID id,
			@Valid @RequestBody AnularMovimientoRequest request
	) {
		movimientoService.anular(id, request.usuarioId(), request.motivoAnulacion());
	}
}
