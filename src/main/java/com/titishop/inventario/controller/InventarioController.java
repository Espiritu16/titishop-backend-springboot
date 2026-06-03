package com.titishop.inventario.controller;

import com.titishop.inventario.dto.ActualizarInventarioRequest;
import com.titishop.inventario.dto.CrearInventarioRequest;
import com.titishop.inventario.dto.InventarioResponse;
import com.titishop.inventario.service.InventarioService;
import com.titishop.compartido.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventario")
@Tag(name = "Inventario", description = "Gestion de inventario por producto.")
@SecurityRequirement(name = "bearerAuth")
public class InventarioController {

	private final InventarioService inventarioService;

	public InventarioController(InventarioService inventarioService) {
		this.inventarioService = inventarioService;
	}

	@GetMapping
	@Operation(summary = "Listar inventarios", description = "Obtiene todos los registros de inventario disponibles.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Listado obtenido correctamente.",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = InventarioResponse.class)))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public List<InventarioResponse> listar() {
		return inventarioService.listar();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener inventario por ID", description = "Busca un registro de inventario por su identificador.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Inventario encontrado.",
					content = @Content(schema = @Schema(implementation = InventarioResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Inventario no encontrado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public InventarioResponse obtenerPorId(@Parameter(description = "ID del inventario.", example = "7e11af9c-a8b3-4b63-b89f-2ac3f6852101") @PathVariable UUID id) {
		return inventarioService.obtenerPorId(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Crear inventario", description = "Registra un inventario inicial para un producto.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			description = "Datos del inventario.",
			content = @Content(
					schema = @Schema(implementation = CrearInventarioRequest.class),
					examples = @ExampleObject(
							name = "Nuevo inventario",
							value = """
									{
									  "productoId": "4f55a1cc-a3f8-4be1-83ab-9b9f4c9c1001",
									  "stockActual": 120,
									  "stockMinimo": 20,
									  "ubicacion": "A1-RACK-03"
									}
									"""
					)
			)
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Inventario creado correctamente.",
					content = @Content(schema = @Schema(implementation = InventarioResponse.class))),
			@ApiResponse(responseCode = "400", description = "Datos de entrada invalidos.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Producto no encontrado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Ya existe inventario para el producto enviado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "422", description = "Producto inactivo o regla de negocio invalida.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public InventarioResponse crear(@Valid @RequestBody CrearInventarioRequest request) {
		return inventarioService.crear(request);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar inventario", description = "Actualiza configuracion operativa y estado de un inventario.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			description = "Datos actualizados del inventario.",
			content = @Content(
					schema = @Schema(implementation = ActualizarInventarioRequest.class),
					examples = @ExampleObject(
							name = "Actualizar inventario",
							value = """
									{
									  "stockMinimo": 18,
									  "ubicacion": "A1-RACK-04",
									  "estado": "ACTIVO"
									}
									"""
					)
			)
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Inventario actualizado correctamente.",
					content = @Content(schema = @Schema(implementation = InventarioResponse.class))),
			@ApiResponse(responseCode = "400", description = "Datos de entrada invalidos.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Inventario no encontrado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "422", description = "Regla de negocio invalida para el inventario.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public InventarioResponse actualizar(
			@Parameter(description = "ID del inventario a actualizar.", example = "7e11af9c-a8b3-4b63-b89f-2ac3f6852101")
			@PathVariable UUID id,
			@Valid @RequestBody ActualizarInventarioRequest request
	) {
		return inventarioService.actualizar(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Inactivar inventario", description = "Marca un inventario como inactivo sin eliminarlo.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Inventario inactivado correctamente."),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Inventario no encontrado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public void inactivar(@Parameter(description = "ID del inventario a inactivar.", example = "7e11af9c-a8b3-4b63-b89f-2ac3f6852101") @PathVariable UUID id) {
		inventarioService.inactivar(id);
	}
}
