package com.titishop.proveedores.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CrearProveedorRequest(
		@NotBlank @Size(max = 120) String razonSocial,
		@NotBlank @Pattern(regexp = "\\d{11}", message = "ruc debe tener 11 digitos") String ruc,
		@NotBlank @Pattern(regexp = "\\d{9}", message = "celular debe tener 9 digitos") String celular,
		@NotBlank @Pattern(regexp = "\\d{9}", message = "telefono debe tener 9 digitos") String telefono,
		@NotBlank @Email @Size(max = 160) String email,
		@NotBlank @Size(max = 160) String direccion
) {
}
