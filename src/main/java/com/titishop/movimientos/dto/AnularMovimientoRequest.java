package com.titishop.movimientos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(name = "AnularMovimientoRequest", description = "Payload para anular un movimiento previamente registrado.")
public record AnularMovimientoRequest(
		@Schema(description = "Identificador del usuario que anula el movimiento.", example = "8ddf1f08-6f9d-4d17-9c42-a8b4d6bfc001")
		@NotNull UUID usuarioId,
		@Schema(description = "Motivo de la anulacion.", example = "Registro duplicado por error de digitacion.", maxLength = 255)
		@NotBlank @Size(max = 255) String motivoAnulacion
) {
}
