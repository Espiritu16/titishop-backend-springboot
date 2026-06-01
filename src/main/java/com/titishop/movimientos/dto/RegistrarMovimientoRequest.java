package com.titishop.movimientos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record RegistrarMovimientoRequest(
		@NotNull UUID productoId,
		UUID proveedorId,
		@NotNull UUID usuarioId,
		@NotNull TipoMovimiento tipo,
		@Positive Integer cantidad,
		@PositiveOrZero Integer stockDestino,
		@NotBlank @Size(max = 255) String motivo
) {
}
