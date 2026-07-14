package com.titishop.autenticacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "SolicitarRecuperacionPasswordRequest")
public record SolicitarRecuperacionPasswordRequest(
		@Schema(description = "Correo registrado del usuario.", example = "admin@titishop.pe")
		@NotBlank @Email String email
) {
}
