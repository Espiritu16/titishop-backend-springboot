package com.titishop.proveedores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "CrearProveedorRequest", description = "Payload para registrar un proveedor.")
public record CrearProveedorRequest(
		@Schema(description = "Razon social del proveedor.", example = "Distribuidora Lima Norte SAC", maxLength = 120)
		@NotBlank @Size(max = 120) String razonSocial,
		@Schema(description = "RUC del proveedor.", example = "20123456789", pattern = "\\d{11}")
		@NotBlank @Pattern(regexp = "\\d{11}", message = "ruc debe tener 11 digitos") String ruc,
		@Schema(description = "Celular de contacto.", example = "987654321", pattern = "\\d{9}")
		@NotBlank @Pattern(regexp = "\\d{9}", message = "celular debe tener 9 digitos") String celular,
		@Schema(description = "Telefono de oficina.", example = "014567890", pattern = "\\d{9}")
		@NotBlank @Pattern(regexp = "\\d{9}", message = "telefono debe tener 9 digitos") String telefono,
		@Schema(description = "Correo de contacto.", example = "ventas@limanorte.pe", maxLength = 160)
		@NotBlank @Email @Size(max = 160) String email,
		@Schema(description = "Direccion comercial.", example = "Av. Los Almacenes 123, Independencia", maxLength = 160)
		@NotBlank @Size(max = 160) String direccion
) {
}
