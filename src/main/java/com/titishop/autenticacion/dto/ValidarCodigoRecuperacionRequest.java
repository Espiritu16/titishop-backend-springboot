package com.titishop.autenticacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(name = "ValidarCodigoRecuperacionRequest")
public record ValidarCodigoRecuperacionRequest(
		@NotBlank @Email String email,
		@Schema(description = "Código de 6 dígitos enviado al correo.", example = "123456")
		@NotBlank @Pattern(regexp = "\\d{6}", message = "Código debe tener 6 dígitos") String codigo
) {
}
