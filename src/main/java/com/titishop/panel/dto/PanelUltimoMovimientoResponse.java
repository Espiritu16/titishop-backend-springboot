package com.titishop.panel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.titishop.movimientos.dto.TipoMovimiento;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "PanelUltimoMovimientoResponse", description = "Movimiento resumido mostrado en el panel.")
public record PanelUltimoMovimientoResponse(
		@Schema(description = "Identificador del movimiento.", example = "2d11af9c-a8b3-4b63-b89f-2ac3f6852199")
		UUID id,
		@Schema(description = "Fecha del movimiento.", example = "2026-06-02T14:10:00Z")
		Instant fecha,
		@Schema(description = "Producto involucrado.", example = "Leche Evaporada Entera 410g")
		String productoNombre,
		@Schema(description = "SKU del producto.", example = "LEC-410-001")
		String productoSku,
		@Schema(description = "Tipo de movimiento.", example = "ENTRADA")
		TipoMovimiento tipo,
		@Schema(description = "Cantidad movilizada.", example = "24")
		Integer cantidad,
		@Schema(description = "Usuario que registro el movimiento.", example = "Kevin Espiritu Castillo")
		String creadoPorNombre
) {
}
