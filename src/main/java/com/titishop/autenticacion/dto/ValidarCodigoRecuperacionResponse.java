package com.titishop.autenticacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ValidarCodigoRecuperacionResponse")
public record ValidarCodigoRecuperacionResponse(
		@Schema(description = "Token temporal para restablecer la contraseña.")
		String resetToken
) {
}
