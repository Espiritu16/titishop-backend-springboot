package com.titishop.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(name = "CrearInventarioRequest", description = "Payload para registrar un inventario asociado a un producto.")
public record CrearInventarioRequest(
		@Schema(description = "Identificador del producto asociado.", example = "4f55a1cc-a3f8-4be1-83ab-9b9f4c9c1001")
		@NotNull UUID productoId,
		@Schema(description = "Stock inicial disponible.", example = "120", minimum = "0")
		@NotNull @Min(0) Integer stockActual,
		@Schema(description = "Stock minimo permitido antes de alerta.", example = "20", minimum = "0")
		@NotNull @Min(0) Integer stockMinimo,
		@Schema(description = "Ubicación física en almacén.", example = "A1-RACK-03", maxLength = 40)
		@NotBlank @Size(max = 40) String ubicacion
) {
}
