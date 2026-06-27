package com.titishop.proveedores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "ActualizarEstadoProveedorRequest", description = "Payload para cambiar solo el estado de un proveedor.")
public record ActualizarEstadoProveedorRequest(
		@Schema(description = "Nuevo estado del proveedor.", example = "INACTIVO")
		@NotNull EstadoProveedor estado
) {
}
