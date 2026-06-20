package com.titishop.productos.controller;

import com.titishop.productos.dto.ActualizarMarcaRequest;
import com.titishop.productos.dto.CrearMarcaRequest;
import com.titishop.productos.dto.EstadoCatalogo;
import com.titishop.productos.dto.MarcaResponse;
import com.titishop.productos.service.MarcaService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/marcas")
@Tag(name = "Marcas", description = "Gestion de marcas comerciales del catalogo.")
@SecurityRequirement(name = "bearerAuth")
public class MarcaController {

	private final MarcaService marcaService;

	public MarcaController(MarcaService marcaService) {
		this.marcaService = marcaService;
	}

	@GetMapping
	@Operation(summary = "Listar marcas", description = "Obtiene todas las marcas registradas.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Listado obtenido correctamente.",
					content = @Content(schema = @Schema(implementation = PaginaResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public PaginaResponse<MarcaResponse> listar(
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
			@RequestParam(required = false) String busqueda,
			@RequestParam(required = false) EstadoCatalogo estado
	) {
		return marcaService.listar(page, size, busqueda, estado);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener marca por ID", description = "Busca una marca especifica por su identificador.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Marca encontrada.",
					content = @Content(schema = @Schema(implementation = MarcaResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Marca no encontrada.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public MarcaResponse obtenerPorId(@Parameter(description = "ID de la marca.", example = "1ab2cd34-56ef-7890-ab12-cd34ef567890") @PathVariable UUID id) {
		return marcaService.obtenerPorId(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Crear marca", description = "Registra una nueva marca comercial.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			description = "Datos de la marca.",
			content = @Content(
					schema = @Schema(implementation = CrearMarcaRequest.class),
					examples = @ExampleObject(
							name = "Nueva marca",
							value = """
									{
									  "nombre": "Gloria"
									}
									"""
					)
			)
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Marca creada correctamente.",
					content = @Content(schema = @Schema(implementation = MarcaResponse.class))),
			@ApiResponse(responseCode = "400", description = "Datos de entrada invalidos.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "409", description = "Ya existe una marca con ese nombre.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public MarcaResponse crear(@Valid @RequestBody CrearMarcaRequest request) {
		return marcaService.crear(request);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar marca", description = "Actualiza el nombre o estado de una marca.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			description = "Datos actualizados de la marca.",
			content = @Content(
					schema = @Schema(implementation = ActualizarMarcaRequest.class),
					examples = @ExampleObject(
							name = "Actualizar marca",
							value = """
									{
									  "nombre": "Gloria Premium",
									  "estado": "ACTIVO"
									}
									"""
					)
			)
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Marca actualizada correctamente.",
					content = @Content(schema = @Schema(implementation = MarcaResponse.class))),
			@ApiResponse(responseCode = "400", description = "Datos de entrada invalidos.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Marca no encontrada.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Ya existe una marca con ese nombre.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public MarcaResponse actualizar(
			@Parameter(description = "ID de la marca a actualizar.", example = "1ab2cd34-56ef-7890-ab12-cd34ef567890")
			@PathVariable UUID id,
			@Valid @RequestBody ActualizarMarcaRequest request
	) {
		return marcaService.actualizar(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Inactivar marca", description = "Marca un registro como inactivo sin eliminarlo.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Marca inactivada correctamente."),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Marca no encontrada.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public void inactivar(@Parameter(description = "ID de la marca a inactivar.", example = "1ab2cd34-56ef-7890-ab12-cd34ef567890") @PathVariable UUID id) {
		marcaService.inactivar(id);
	}
}
