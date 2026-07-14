package com.titishop.autenticacion.controller;

import com.titishop.autenticacion.dto.LoginRequest;
import com.titishop.autenticacion.dto.LoginResponse;
import com.titishop.autenticacion.dto.MensajeResponse;
import com.titishop.autenticacion.dto.RestablecerPasswordRequest;
import com.titishop.autenticacion.dto.SolicitarRecuperacionPasswordRequest;
import com.titishop.autenticacion.dto.ValidarCodigoRecuperacionRequest;
import com.titishop.autenticacion.dto.ValidarCodigoRecuperacionResponse;
import com.titishop.autenticacion.service.RecuperacionPasswordService;
import com.titishop.autenticacion.service.AutenticacionService;
import com.titishop.compartido.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/autenticacion")
@Tag(name = "Autenticacion", description = "Endpoints publicos para iniciar sesion y obtener JWT.")
public class AutenticacionController {

	private final AutenticacionService autenticacionService;
	private final RecuperacionPasswordService recuperacionPasswordService;

	public AutenticacionController(
			AutenticacionService autenticacionService,
			RecuperacionPasswordService recuperacionPasswordService
	) {
		this.autenticacionService = autenticacionService;
		this.recuperacionPasswordService = recuperacionPasswordService;
	}

	@PostMapping("/login")
	@ResponseStatus(HttpStatus.OK)
	@Operation(
			summary = "Iniciar sesion",
			description = "Autentica un usuario con correo y contrasena, y devuelve un token JWT para consumir endpoints protegidos."
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			description = "Credenciales de acceso.",
			content = @Content(
					schema = @Schema(implementation = LoginRequest.class),
					examples = @ExampleObject(
							name = "Login",
							value = """
									{
									  "email": "kevin@gmail.com",
									  "password": "kevin123"
									}
									"""
					)
			)
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Login exitoso.",
					content = @Content(schema = @Schema(implementation = LoginResponse.class))),
			@ApiResponse(responseCode = "400", description = "Credenciales con formato invalido.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Credenciales incorrectas."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public LoginResponse login(@Valid @RequestBody LoginRequest request) {
		return autenticacionService.login(request);
	}

	@PostMapping("/recuperacion/solicitar")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Solicitar codigo de recuperacion")
	public MensajeResponse solicitarRecuperacion(@Valid @RequestBody SolicitarRecuperacionPasswordRequest request) {
		return recuperacionPasswordService.solicitar(request);
	}

	@PostMapping("/recuperacion/validar")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Validar codigo de recuperacion")
	public ValidarCodigoRecuperacionResponse validarCodigo(@Valid @RequestBody ValidarCodigoRecuperacionRequest request) {
		return recuperacionPasswordService.validar(request);
	}

	@PostMapping("/recuperacion/restablecer")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Restablecer contrasena")
	public MensajeResponse restablecerPassword(@Valid @RequestBody RestablecerPasswordRequest request) {
		return recuperacionPasswordService.restablecer(request);
	}
}
