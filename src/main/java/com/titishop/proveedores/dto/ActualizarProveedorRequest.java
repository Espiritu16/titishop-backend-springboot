package com.titishop.proveedores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "ActualizarProveedorRequest", description = "Payload para actualizar los datos de un proveedor.")
public record ActualizarProveedorRequest(
		@Schema(description = "Razón social actualizada.", example = "Distribuidora Lima Norte SAC", maxLength = 120)
		@NotBlank @Size(max = 120) String razonSocial,
		@Schema(description = "RUC actualizado.", example = "20123456789", pattern = "\\d{11}")
		@NotBlank @Pattern(regexp = "\\d{11}", message = "RUC debe tener 11 dígitos") String ruc,
		@Schema(description = "Celular actualizado.", example = "987654321", pattern = "\\d{9}")
		@Pattern(regexp = "^$|\\d{9}", message = "Celular debe tener 9 dígitos") String celular,
		@Schema(description = "Teléfono actualizado.", example = "014567890", pattern = "\\d{9}")
		@Pattern(regexp = "^$|\\d{9}", message = "Teléfono debe tener 9 dígitos") String telefono,
		@Schema(description = "Correo actualizado.", example = "compras@limanorte.pe", maxLength = 160)
		@Email @Size(max = 160) String email,
		@Schema(description = "Dirección actualizada.", example = "Av. Los Almacenes 321, Independencia", maxLength = 160)
		@NotBlank @Size(max = 160) String direccion,
		@Schema(description = "Estado del proveedor.", example = "ACTIVO")
		@NotNull EstadoProveedor estado
) {
}
