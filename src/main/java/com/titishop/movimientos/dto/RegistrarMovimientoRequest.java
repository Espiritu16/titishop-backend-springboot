package com.titishop.movimientos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(name = "RegistrarMovimientoRequest", description = "Payload para registrar entradas, salidas o ajustes de inventario.")
public record RegistrarMovimientoRequest(
		@Schema(description = "Identificador del producto afectado.", example = "4f55a1cc-a3f8-4be1-83ab-9b9f4c9c1001")
		@NotNull UUID productoId,
		@Schema(description = "Proveedor relacionado. Requerido para movimientos de entrada.", example = "5a81e2d0-55f8-4a3b-8d65-febec9959002", nullable = true)
		UUID proveedorId,
		@Schema(description = "Usuario que registra el movimiento.", example = "8ddf1f08-6f9d-4d17-9c42-a8b4d6bfc001")
		@NotNull UUID usuarioId,
		@Schema(description = "Tipo de movimiento.", example = "ENTRADA")
		@NotNull TipoMovimiento tipo,
		@Schema(description = "Cantidad de unidades involucradas.", example = "24", minimum = "1")
		@Positive Integer cantidad,
		@Schema(description = "Stock destino esperado para ajustes.", example = "140", minimum = "0", nullable = true)
		@PositiveOrZero Integer stockDestino,
		@Schema(description = "Motivo operativo del movimiento.", example = "Ingreso por compra de reposicion semanal.", maxLength = 255)
		@NotBlank @Size(max = 255) String motivo
) {
}
