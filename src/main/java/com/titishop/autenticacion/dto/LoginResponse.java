package com.titishop.autenticacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(name = "LoginResponse", description = "Datos de autenticacion generados luego de un login exitoso.")
public record LoginResponse(
		@Schema(description = "JWT firmado para consumir endpoints protegidos.", example = "eyJhbGciOiJIUzI1NiJ9.token-ficticio.swagger")
		String token,
		@Schema(description = "Tipo de token emitido.", example = "Bearer")
		String tipo,
		@Schema(description = "Fecha de expiracion del token.", example = "2026-06-02T23:59:59Z")
		Instant expiraEn,
		@Schema(description = "Nombre completo del usuario autenticado.", example = "Administrador TitiShop")
		String nombreCompleto,
		@Schema(description = "Correo del usuario autenticado.", example = "admin@titishop.com")
		String email,
		@Schema(description = "Rol asignado al usuario autenticado.", example = "ADMINISTRADOR")
		String rol
) {
}
