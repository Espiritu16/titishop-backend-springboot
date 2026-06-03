package com.titishop.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.titishop.usuarios.entity.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "CrearUsuarioRequest", description = "Payload para registrar un nuevo usuario del sistema.")
public record CrearUsuarioRequest(
		@Schema(description = "Nombre completo del usuario.", example = "Kevin Espiritu Castillo", maxLength = 120)
		@NotBlank @Size(max = 120) String nombreCompleto,
		@Schema(description = "Correo unico del usuario.", example = "kevin@titishop.com", maxLength = 160)
		@NotBlank @Email @Size(max = 160) String email,
		@Schema(description = "Contrasena inicial del usuario.", example = "Supervisor123*", minLength = 8, maxLength = 120)
		@NotBlank @Size(min = 8, max = 120) String password,
		@Schema(description = "Rol operativo del usuario.", example = "SUPERVISOR")
		@NotNull RolUsuario rol
) {
}
