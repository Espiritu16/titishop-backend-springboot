package com.titishop.autenticacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginRequest", description = "Credenciales para autenticar un usuario en TitiShop.")
public record LoginRequest(
		@Schema(description = "Correo del usuario registrado.", example = "kevin@gmail.com")
		@NotBlank @Email String email,
		@Schema(description = "Contrasena del usuario.", example = "kevin123")
		@NotBlank String password
) {
}
