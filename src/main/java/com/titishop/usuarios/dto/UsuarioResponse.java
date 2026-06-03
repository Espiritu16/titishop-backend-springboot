package com.titishop.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.titishop.usuarios.entity.EstadoUsuario;
import com.titishop.usuarios.entity.RolUsuario;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "UsuarioResponse", description = "Usuario expuesto por la API.")
public record UsuarioResponse(
		@Schema(description = "Identificador del usuario.", example = "8ddf1f08-6f9d-4d17-9c42-a8b4d6bfc001")
		UUID id,
		@Schema(description = "Nombre completo del usuario.", example = "Kevin Espiritu Castillo")
		String nombreCompleto,
		@Schema(description = "Correo del usuario.", example = "kevin@titishop.com")
		String email,
		@Schema(description = "Rol del usuario.", example = "SUPERVISOR")
		RolUsuario rol,
		@Schema(description = "Estado del usuario.", example = "ACTIVO")
		EstadoUsuario estado,
		@Schema(description = "Fecha de creacion.", example = "2026-06-01T10:15:30Z")
		Instant creadoEn,
		@Schema(description = "Fecha de ultima actualizacion.", example = "2026-06-02T14:45:00Z")
		Instant actualizadoEn
) {
}
