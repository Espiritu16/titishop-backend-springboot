package com.titishop.autenticacion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.titishop.autenticacion.dto.RestablecerPasswordRequest;
import com.titishop.autenticacion.dto.SolicitarRecuperacionPasswordRequest;
import com.titishop.autenticacion.dto.ValidarCodigoRecuperacionRequest;
import com.titishop.autenticacion.entity.PasswordResetCode;
import com.titishop.autenticacion.repository.PasswordResetCodeRepository;
import com.titishop.autenticacion.service.CodigoRecuperacionGenerator;
import com.titishop.autenticacion.service.RecuperacionPasswordMailService;
import com.titishop.autenticacion.service.RecuperacionPasswordService;
import com.titishop.usuarios.entity.EstadoUsuario;
import com.titishop.usuarios.entity.RolUsuario;
import com.titishop.usuarios.entity.Usuario;
import com.titishop.usuarios.repository.UsuarioRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class RecuperacionPasswordServiceTests {

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private PasswordResetCodeRepository resetCodeRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private RecuperacionPasswordMailService mailService;

	private RecuperacionPasswordService service;

	private final Clock clock = Clock.fixed(Instant.parse("2026-07-14T00:00:00Z"), ZoneOffset.UTC);

	@BeforeEach
	void setUp() {
		CodigoRecuperacionGenerator generator = new CodigoRecuperacionGenerator() {
			@Override
			public String generarCodigo() {
				return "123456";
			}

			@Override
			public String generarResetToken() {
				return "reset-token-plano";
			}
		};
		service = new RecuperacionPasswordService(
				usuarioRepository,
				resetCodeRepository,
				passwordEncoder,
				mailService,
				generator,
				clock,
				10,
				5
		);
	}

	@Test
	void solicitarGeneraCodigoHasheadoYEnviaCorreo() {
		Usuario usuario = new Usuario("Admin", "admin@titishop.pe", "hash", RolUsuario.ADMINISTRADOR);
		when(usuarioRepository.findByEmailIgnoreCase("admin@titishop.pe")).thenReturn(Optional.of(usuario));
		when(passwordEncoder.encode("123456")).thenReturn("hash-codigo");
		when(resetCodeRepository.save(any(PasswordResetCode.class))).thenAnswer(invocation -> invocation.getArgument(0));

		service.solicitar(new SolicitarRecuperacionPasswordRequest(" ADMIN@TITISHOP.PE "));

		ArgumentCaptor<PasswordResetCode> captor = ArgumentCaptor.forClass(PasswordResetCode.class);
		verify(resetCodeRepository).save(captor.capture());
		assertThat(captor.getValue().getCodigoHash()).isEqualTo("hash-codigo");
		assertThat(captor.getValue().getCodigoHash()).doesNotContain("123456");
		assertThat(captor.getValue().getExpiraEn()).isEqualTo(Instant.parse("2026-07-14T00:10:00Z"));
		verify(mailService).enviarCodigo("admin@titishop.pe", "Admin", "123456");
	}

	@Test
	void validarCodigoCorrectoDevuelveResetTokenYLoGuardaHasheado() {
		Usuario usuario = new Usuario("Admin", "admin@titishop.pe", "hash", RolUsuario.ADMINISTRADOR);
		PasswordResetCode resetCode = new PasswordResetCode(usuario, "hash-codigo", Instant.parse("2026-07-14T00:10:00Z"), clock.instant());
		when(resetCodeRepository.findTopByUsuarioEmailIgnoreCaseAndUsadoEnIsNullOrderByCreadoEnDesc("admin@titishop.pe"))
				.thenReturn(Optional.of(resetCode));
		when(passwordEncoder.matches("123456", "hash-codigo")).thenReturn(true);
		when(passwordEncoder.encode("reset-token-plano")).thenReturn("hash-token");

		var response = service.validar(new ValidarCodigoRecuperacionRequest("admin@titishop.pe", "123456"));

		assertThat(response.resetToken()).isEqualTo("reset-token-plano");
		assertThat(resetCode.getResetTokenHash()).isEqualTo("hash-token");
		assertThat(resetCode.getValidadoEn()).isEqualTo(clock.instant());
		verify(resetCodeRepository).save(resetCode);
	}

	@Test
	void restablecerActualizaPasswordYMarcaCodigoComoUsado() {
		Usuario usuario = new Usuario("Admin", "admin@titishop.pe", "hash-anterior", RolUsuario.ADMINISTRADOR);
		PasswordResetCode resetCode = new PasswordResetCode(usuario, "hash-codigo", Instant.parse("2026-07-14T00:10:00Z"), clock.instant());
		resetCode.marcarValidado("hash-token", clock.instant());
		when(resetCodeRepository.findTopByUsuarioEmailIgnoreCaseAndUsadoEnIsNullOrderByCreadoEnDesc("admin@titishop.pe"))
				.thenReturn(Optional.of(resetCode));
		when(passwordEncoder.matches("reset-token-plano", "hash-token")).thenReturn(true);
		when(passwordEncoder.encode("NuevaClave123")).thenReturn("hash-nuevo");

		service.restablecer(new RestablecerPasswordRequest("admin@titishop.pe", "reset-token-plano", "NuevaClave123"));

		assertThat(usuario.getPasswordHash()).isEqualTo("hash-nuevo");
		assertThat(resetCode.getUsadoEn()).isEqualTo(clock.instant());
		verify(resetCodeRepository).save(resetCode);
		verify(usuarioRepository).save(usuario);
	}

	@Test
	void solicitarNoEnviaCorreoSiUsuarioEstaInactivo() {
		Usuario usuario = new Usuario("Admin", "admin@titishop.pe", "hash", RolUsuario.ADMINISTRADOR);
		usuario.inactivar();
		when(usuarioRepository.findByEmailIgnoreCase("admin@titishop.pe")).thenReturn(Optional.of(usuario));

		service.solicitar(new SolicitarRecuperacionPasswordRequest("admin@titishop.pe"));

		verify(resetCodeRepository, org.mockito.Mockito.never()).save(any());
		verify(mailService, org.mockito.Mockito.never()).enviarCodigo(any(), any(), any());
		assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.INACTIVO);
	}
}
