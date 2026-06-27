package com.titishop.usuarios.dto;

import com.titishop.usuarios.entity.EstadoUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "ActualizarEstadoUsuarioRequest", description = "Payload para cambiar solo el estado de un usuario.")
public record ActualizarEstadoUsuarioRequest(
		@Schema(description = "Nuevo estado del usuario.", example = "INACTIVO")
		@NotNull EstadoUsuario estado
) {
}
