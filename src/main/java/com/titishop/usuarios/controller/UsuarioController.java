package com.titishop.usuarios.controller;

import com.titishop.usuarios.dto.ActualizarUsuarioRequest;
import com.titishop.usuarios.dto.CrearUsuarioRequest;
import com.titishop.usuarios.dto.UsuarioResponse;
import com.titishop.usuarios.service.UsuarioService;
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
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Administracion de usuarios internos de TitiShop.")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

	private final UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@GetMapping
	@Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios registrados en el sistema.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Listado obtenido correctamente.",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = UsuarioResponse.class)))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public List<UsuarioResponse> listar() {
		return usuarioService.listar();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener usuario por ID", description = "Busca un usuario especifico por su identificador.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Usuario encontrado.",
					content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public UsuarioResponse obtenerPorId(@Parameter(description = "ID del usuario.", example = "8ddf1f08-6f9d-4d17-9c42-a8b4d6bfc001") @PathVariable UUID id) {
		return usuarioService.obtenerPorId(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Crear usuario", description = "Registra un nuevo usuario con rol y credenciales iniciales.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			description = "Datos del nuevo usuario.",
			content = @Content(
					schema = @Schema(implementation = CrearUsuarioRequest.class),
					examples = @ExampleObject(
							name = "Nuevo usuario",
							value = """
									{
									  "nombreCompleto": "Kevin Espiritu Castillo",
									  "email": "kevin@titishop.com",
									  "password": "Supervisor123*",
									  "rol": "SUPERVISOR"
									}
									"""
					)
			)
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Usuario creado correctamente.",
					content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
			@ApiResponse(responseCode = "400", description = "Datos de entrada invalidos.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "409", description = "Ya existe un usuario con el correo enviado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public UsuarioResponse crear(@Valid @RequestBody CrearUsuarioRequest request) {
		return usuarioService.crear(request);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar usuario", description = "Actualiza perfil, rol, estado y opcionalmente la contrasena de un usuario.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			description = "Datos actualizados del usuario.",
			content = @Content(
					schema = @Schema(implementation = ActualizarUsuarioRequest.class),
					examples = @ExampleObject(
							name = "Actualizar usuario",
							value = """
									{
									  "nombreCompleto": "Kevin Espiritu Castillo",
									  "email": "kevin.espiritu@titishop.com",
									  "password": "NuevaClave123*",
									  "rol": "ADMINISTRADOR",
									  "estado": "ACTIVO"
									}
									"""
					)
			)
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente.",
					content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
			@ApiResponse(responseCode = "400", description = "Datos de entrada invalidos.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Ya existe un usuario con el correo enviado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public UsuarioResponse actualizar(
			@Parameter(description = "ID del usuario a actualizar.", example = "8ddf1f08-6f9d-4d17-9c42-a8b4d6bfc001")
			@PathVariable UUID id,
			@Valid @RequestBody ActualizarUsuarioRequest request
	) {
		return usuarioService.actualizar(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Inactivar usuario", description = "Marca un usuario como inactivo sin eliminarlo fisicamente.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Usuario inactivado correctamente."),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public void inactivar(@Parameter(description = "ID del usuario a inactivar.", example = "8ddf1f08-6f9d-4d17-9c42-a8b4d6bfc001") @PathVariable UUID id) {
		usuarioService.inactivar(id);
	}
}
