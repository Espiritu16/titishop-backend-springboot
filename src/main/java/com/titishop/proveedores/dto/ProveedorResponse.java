package com.titishop.proveedores.dto;

import java.time.Instant;
import java.util.UUID;

public record ProveedorResponse(
		UUID id,
		String razonSocial,
		String ruc,
		String celular,
		String telefono,
		String email,
		String direccion,
		EstadoProveedor estado,
		Instant creadoEn,
		Instant actualizadoEn
) {
}
