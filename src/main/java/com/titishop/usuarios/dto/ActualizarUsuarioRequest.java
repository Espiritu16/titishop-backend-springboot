package com.titishop.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.titishop.usuarios.entity.EstadoUsuario;
import com.titishop.usuarios.entity.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "ActualizarUsuarioRequest", description = "Payload para actualizar datos y estado de un usuario.")
public record ActualizarUsuarioRequest(
		@Schema(description = "Nombre completo actualizado del usuario.", example = "Kevin Espiritu Castillo", maxLength = 120)
		@NotBlank @Size(max = 120) String nombreCompleto,
		@Schema(description = "Correo actualizado del usuario.", example = "kevin.espiritu@titishop.com", maxLength = 160)
		@NotBlank @Email @Size(max = 160) String email,
		@Schema(description = "Nueva contrasena. Si se envía, no puede contener solo espacios.", example = "NuevaClave123*", minLength = 8, maxLength = 120, nullable = true)
		@Pattern(regexp = ".*\\S.*", message = "password no debe contener solo espacios")
		@Size(min = 8, max = 120) String password,
		@Schema(description = "Rol vigente del usuario.", example = "ADMINISTRADOR")
		@NotNull RolUsuario rol,
		@Schema(description = "Estado vigente del usuario.", example = "ACTIVO")
		@NotNull EstadoUsuario estado
) {
}
