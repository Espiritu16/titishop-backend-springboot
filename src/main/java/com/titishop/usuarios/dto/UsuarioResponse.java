package com.titishop.usuarios.dto;

import com.titishop.usuarios.entity.EstadoUsuario;
import com.titishop.usuarios.entity.RolUsuario;
import java.time.Instant;
import java.util.UUID;

public record UsuarioResponse(
		UUID id,
		String nombreCompleto,
		String email,
		RolUsuario rol,
		EstadoUsuario estado,
		Instant creadoEn,
		Instant actualizadoEn
) {
}
