package com.titishop.usuarios.dto;

import com.titishop.usuarios.entity.EstadoUsuario;
import com.titishop.usuarios.entity.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActualizarUsuarioRequest(
		@NotBlank @Size(max = 120) String nombreCompleto,
		@NotBlank @Email @Size(max = 160) String email,
		@Size(min = 8, max = 120) String password,
		@NotNull RolUsuario rol,
		@NotNull EstadoUsuario estado
) {
}
