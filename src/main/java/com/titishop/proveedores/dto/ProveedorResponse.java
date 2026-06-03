package com.titishop.proveedores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "ProveedorResponse", description = "Proveedor comercial registrado en el sistema.")
public record ProveedorResponse(
		@Schema(description = "Identificador del proveedor.", example = "5a81e2d0-55f8-4a3b-8d65-febec9959002")
		UUID id,
		@Schema(description = "Razon social.", example = "Distribuidora Lima Norte SAC")
		String razonSocial,
		@Schema(description = "RUC.", example = "20123456789")
		String ruc,
		@Schema(description = "Celular de contacto.", example = "987654321")
		String celular,
		@Schema(description = "Telefono de contacto.", example = "014567890")
		String telefono,
		@Schema(description = "Correo de contacto.", example = "compras@limanorte.pe")
		String email,
		@Schema(description = "Direccion comercial.", example = "Av. Los Almacenes 321, Independencia")
		String direccion,
		@Schema(description = "Estado del proveedor.", example = "ACTIVO")
		EstadoProveedor estado,
		@Schema(description = "Fecha de creacion.", example = "2026-06-01T09:15:00Z")
		Instant creadoEn,
		@Schema(description = "Fecha de ultima actualizacion.", example = "2026-06-02T15:10:00Z")
		Instant actualizadoEn
) {
}
