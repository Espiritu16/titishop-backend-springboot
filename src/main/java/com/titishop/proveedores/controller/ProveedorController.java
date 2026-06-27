package com.titishop.proveedores.controller;

import com.titishop.proveedores.dto.ActualizarProveedorRequest;
import com.titishop.proveedores.dto.ActualizarEstadoProveedorRequest;
import com.titishop.proveedores.dto.ConsultaRucProveedorResponse;
import com.titishop.proveedores.dto.CrearProveedorRequest;
import com.titishop.proveedores.dto.EstadoProveedor;
import com.titishop.proveedores.dto.ProveedorResponse;
import com.titishop.proveedores.service.ProveedorService;
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
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/proveedores")
@Tag(name = "Proveedores", description = "Gestion y consulta de proveedores.")
@SecurityRequirement(name = "bearerAuth")
public class ProveedorController {

	private final ProveedorService proveedorService;

	public ProveedorController(ProveedorService proveedorService) {
		this.proveedorService = proveedorService;
	}

	@GetMapping
	@Operation(summary = "Listar proveedores", description = "Obtiene todos los proveedores registrados.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Listado obtenido correctamente.",
					content = @Content(schema = @Schema(implementation = PaginaResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public PaginaResponse<ProveedorResponse> listar(
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
			@RequestParam(required = false) String busqueda,
			@RequestParam(required = false) EstadoProveedor estado
	) {
		return proveedorService.listar(page, size, busqueda, estado);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener proveedor por ID", description = "Busca un proveedor especifico por su identificador.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Proveedor encontrado.",
					content = @Content(schema = @Schema(implementation = ProveedorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Proveedor no encontrado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ProveedorResponse obtenerPorId(@Parameter(description = "ID del proveedor.", example = "5a81e2d0-55f8-4a3b-8d65-febec9959002") @PathVariable UUID id) {
		return proveedorService.obtenerPorId(id);
	}

	@GetMapping("/consulta-ruc/{ruc}")
	@Operation(summary = "Consultar RUC", description = "Consulta datos tributarios de un proveedor a partir de su numero de RUC.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Consulta realizada correctamente.",
					content = @Content(schema = @Schema(implementation = ConsultaRucProveedorResponse.class))),
			@ApiResponse(responseCode = "400", description = "RUC con formato invalido.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "RUC no encontrado en el servicio externo.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ConsultaRucProveedorResponse consultarRuc(
			@Parameter(description = "RUC de 11 digitos a consultar.", example = "20123456789")
			@PathVariable @Pattern(regexp = "\\d{11}", message = "ruc debe tener 11 digitos") String ruc
	) {
		return proveedorService.consultarRuc(ruc);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Crear proveedor", description = "Registra un nuevo proveedor comercial.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			description = "Datos del proveedor.",
			content = @Content(
					schema = @Schema(implementation = CrearProveedorRequest.class),
					examples = @ExampleObject(
							name = "Nuevo proveedor",
							value = """
									{
									  "razonSocial": "Distribuidora Lima Norte SAC",
									  "ruc": "20123456789",
									  "celular": "987654321",
									  "telefono": "014567890",
									  "email": "ventas@limanorte.pe",
									  "direccion": "Av. Los Almacenes 123, Independencia"
									}
									"""
					)
			)
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Proveedor creado correctamente.",
					content = @Content(schema = @Schema(implementation = ProveedorResponse.class))),
			@ApiResponse(responseCode = "400", description = "Datos de entrada invalidos.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "409", description = "RUC o correo ya registrados.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ProveedorResponse crear(@Valid @RequestBody CrearProveedorRequest request) {
		return proveedorService.crear(request);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar proveedor", description = "Actualiza los datos operativos de un proveedor.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			description = "Datos actualizados del proveedor.",
			content = @Content(
					schema = @Schema(implementation = ActualizarProveedorRequest.class),
					examples = @ExampleObject(
							name = "Actualizar proveedor",
							value = """
									{
									  "razonSocial": "Distribuidora Lima Norte SAC",
									  "ruc": "20123456789",
									  "celular": "987654321",
									  "telefono": "014567890",
									  "email": "compras@limanorte.pe",
									  "direccion": "Av. Los Almacenes 321, Independencia",
									  "estado": "ACTIVO"
									}
									"""
					)
			)
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Proveedor actualizado correctamente.",
					content = @Content(schema = @Schema(implementation = ProveedorResponse.class))),
			@ApiResponse(responseCode = "400", description = "Datos de entrada invalidos.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Proveedor no encontrado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "RUC o correo ya registrados.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ProveedorResponse actualizar(
			@Parameter(description = "ID del proveedor a actualizar.", example = "5a81e2d0-55f8-4a3b-8d65-febec9959002")
			@PathVariable UUID id,
			@Valid @RequestBody ActualizarProveedorRequest request
	) {
		return proveedorService.actualizar(id, request);
	}

	@PatchMapping("/{id}/estado")
	@Operation(summary = "Cambiar estado de proveedor", description = "Actualiza solo el estado de un proveedor.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Estado actualizado correctamente.",
					content = @Content(schema = @Schema(implementation = ProveedorResponse.class))),
			@ApiResponse(responseCode = "400", description = "Datos de entrada invalidos.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Proveedor no encontrado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ProveedorResponse actualizarEstado(
			@Parameter(description = "ID del proveedor.", example = "5a81e2d0-55f8-4a3b-8d65-febec9959002")
			@PathVariable UUID id,
			@Valid @RequestBody ActualizarEstadoProveedorRequest request
	) {
		return proveedorService.actualizarEstado(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Inactivar proveedor", description = "Marca un proveedor como inactivo sin eliminarlo.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Proveedor inactivado correctamente."),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Proveedor no encontrado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public void inactivar(@Parameter(description = "ID del proveedor a inactivar.", example = "5a81e2d0-55f8-4a3b-8d65-febec9959002") @PathVariable UUID id) {
		proveedorService.inactivar(id);
	}
}
