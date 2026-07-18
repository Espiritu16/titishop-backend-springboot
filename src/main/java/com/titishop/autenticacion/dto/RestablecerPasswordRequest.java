package com.titishop.autenticacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "RestablecerPasswordRequest")
public record RestablecerPasswordRequest(
		@NotBlank @Email String email,
		@NotBlank String resetToken,
		@Schema(description = "Nueva contraseña del usuario.", minLength = 8, maxLength = 120)
		@NotBlank @Size(min = 8, max = 120) String nuevaPassword
) {
}
