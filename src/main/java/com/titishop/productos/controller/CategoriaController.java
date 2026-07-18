package com.titishop.productos.controller;

import com.titishop.productos.dto.ActualizarCategoriaRequest;
import com.titishop.productos.dto.ActualizarEstadoCatalogoRequest;
import com.titishop.productos.dto.CategoriaResponse;
import com.titishop.productos.dto.CrearCategoriaRequest;
import com.titishop.productos.dto.EstadoCatalogo;
import com.titishop.productos.service.CategoriaService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/categorias")
@Tag(name = "Categorías", description = "Gestión de categorías del catálogo de productos.")
@SecurityRequirement(name = "bearerAuth")
public class CategoriaController {

	private final CategoriaService categoriaService;

	public CategoriaController(CategoriaService categoriaService) {
		this.categoriaService = categoriaService;
	}

	@GetMapping
	@Operation(summary = "Listar categorías", description = "Obtiene todas las categorías registradas.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Listado obtenido correctamente.",
					content = @Content(schema = @Schema(implementation = PaginaResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public PaginaResponse<CategoriaResponse> listar(
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
			@RequestParam(required = false) String busqueda,
			@RequestParam(required = false) EstadoCatalogo estado
	) {
		return categoriaService.listar(page, size, busqueda, estado);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener categoría por ID", description = "Busca una categoría específica por su identificador.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Categoría encontrada.",
					content = @Content(schema = @Schema(implementation = CategoriaResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Categoría no encontrada.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public CategoriaResponse obtenerPorId(@Parameter(description = "ID de la categoría.", example = "0f1e2d3c-4b5a-6789-9012-3456789abcde") @PathVariable UUID id) {
		return categoriaService.obtenerPorId(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Crear categoría", description = "Registra una nueva categoría para clasificar productos.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			description = "Datos de la categoría.",
			content = @Content(
					schema = @Schema(implementation = CrearCategoriaRequest.class),
					examples = @ExampleObject(
							name = "Nueva categoría",
							value = """
									{
									  "nombre": "Lacteos"
									}
									"""
					)
			)
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Categoría creada correctamente.",
					content = @Content(schema = @Schema(implementation = CategoriaResponse.class))),
			@ApiResponse(responseCode = "400", description = "Datos de entrada inválidos.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "409", description = "Ya existe una categoría con ese nombre.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public CategoriaResponse crear(@Valid @RequestBody CrearCategoriaRequest request) {
		return categoriaService.crear(request);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar categoría", description = "Actualiza el nombre o estado de una categoría existente.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			description = "Datos actualizados de la categoría.",
			content = @Content(
					schema = @Schema(implementation = ActualizarCategoriaRequest.class),
					examples = @ExampleObject(
							name = "Actualizar categoría",
							value = """
									{
									  "nombre": "Lacteos y bebidas",
									  "estado": "ACTIVO"
									}
									"""
					)
			)
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente.",
					content = @Content(schema = @Schema(implementation = CategoriaResponse.class))),
			@ApiResponse(responseCode = "400", description = "Datos de entrada inválidos.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Categoría no encontrada.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Ya existe una categoría con ese nombre.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public CategoriaResponse actualizar(
			@Parameter(description = "ID de la categoría a actualizar.", example = "0f1e2d3c-4b5a-6789-9012-3456789abcde")
			@PathVariable UUID id,
			@Valid @RequestBody ActualizarCategoriaRequest request
	) {
		return categoriaService.actualizar(id, request);
	}

	@PatchMapping("/{id}/estado")
	@Operation(summary = "Cambiar estado de categoría", description = "Actualiza solo el estado de una categoría.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Estado actualizado correctamente.",
					content = @Content(schema = @Schema(implementation = CategoriaResponse.class))),
			@ApiResponse(responseCode = "400", description = "Datos de entrada inválidos.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Categoría no encontrada.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public CategoriaResponse actualizarEstado(
			@Parameter(description = "ID de la categoría.", example = "0f1e2d3c-4b5a-6789-9012-3456789abcde")
			@PathVariable UUID id,
			@Valid @RequestBody ActualizarEstadoCatalogoRequest request
	) {
		return categoriaService.actualizarEstado(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Inactivar categoría", description = "Marca una categoría como inactiva sin eliminarla.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Categoría inactivada correctamente."),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Categoría no encontrada.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public void inactivar(@Parameter(description = "ID de la categoría a inactivar.", example = "0f1e2d3c-4b5a-6789-9012-3456789abcde") @PathVariable UUID id) {
		categoriaService.inactivar(id);
	}
}
