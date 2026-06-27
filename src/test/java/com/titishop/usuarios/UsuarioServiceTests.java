package com.titishop.usuarios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.titishop.usuarios.dto.ActualizarUsuarioRequest;
import com.titishop.usuarios.dto.CrearUsuarioRequest;
import com.titishop.usuarios.entity.EstadoUsuario;
import com.titishop.usuarios.entity.RolUsuario;
import com.titishop.usuarios.entity.Usuario;
import com.titishop.usuarios.exception.EmailUsuarioDuplicadoException;
import com.titishop.usuarios.exception.UsuarioNoEncontradoException;
import com.titishop.usuarios.repository.UsuarioRepository;
import com.titishop.usuarios.service.UsuarioService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTests {

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	private UsuarioService usuarioService;

	@BeforeEach
	void setUp() {
		usuarioService = new UsuarioService(usuarioRepository, passwordEncoder);
	}

	@Test
	void crearFallaSiEmailYaExiste() {
		CrearUsuarioRequest request = new CrearUsuarioRequest("Admin", "admin@titishop.pe", "password123", RolUsuario.ADMINISTRADOR);
		when(usuarioRepository.existsByEmailIgnoreCase("admin@titishop.pe")).thenReturn(true);

		assertThatThrownBy(() -> usuarioService.crear(request))
				.isInstanceOf(EmailUsuarioDuplicadoException.class);
		verify(usuarioRepository, never()).save(any(Usuario.class));
	}

	@Test
	void actualizarFallaSiUsuarioNoExiste() {
		UUID id = UUID.randomUUID();
		ActualizarUsuarioRequest request = new ActualizarUsuarioRequest(
				"Admin Nuevo",
				"nuevo@titishop.pe",
				null,
				RolUsuario.SUPERVISOR,
				EstadoUsuario.ACTIVO
		);

		when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> usuarioService.actualizar(id, request))
				.isInstanceOf(UsuarioNoEncontradoException.class);
	}

	@Test
	void actualizarFallaSiNoHayCambios() {
		UUID id = UUID.randomUUID();
		Usuario usuario = new Usuario("Admin", "admin@titishop.pe", "hash", RolUsuario.ADMINISTRADOR);
		ActualizarUsuarioRequest request = new ActualizarUsuarioRequest(
				" Admin ",
				" ADMIN@TITISHOP.PE ",
				null,
				RolUsuario.ADMINISTRADOR,
				EstadoUsuario.ACTIVO
		);

		when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

		assertThatThrownBy(() -> usuarioService.actualizar(id, request))
				.hasMessage("No hay cambios para actualizar.");
		verify(usuarioRepository, never()).save(usuario);
		verify(passwordEncoder, never()).encode(any());
	}

	@Test
	void inactivarMarcaUsuarioComoInactivo() {
		UUID id = UUID.randomUUID();
		Usuario usuario = new Usuario("Admin", "admin@titishop.pe", "hash", RolUsuario.ADMINISTRADOR);
		when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

		usuarioService.inactivar(id);

		assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.INACTIVO);
		verify(usuarioRepository).save(usuario);
	}
}
