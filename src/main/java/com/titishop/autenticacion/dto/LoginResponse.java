package com.titishop.autenticacion.dto;

import java.time.Instant;

public record LoginResponse(
		String token,
		String tipo,
		Instant expiraEn,
		String nombreCompleto,
		String email,
		String rol
) {
}
