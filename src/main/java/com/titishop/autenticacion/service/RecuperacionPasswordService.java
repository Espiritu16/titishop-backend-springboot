package com.titishop.autenticacion.service;

import com.titishop.autenticacion.dto.MensajeResponse;
import com.titishop.autenticacion.dto.RestablecerPasswordRequest;
import com.titishop.autenticacion.dto.SolicitarRecuperacionPasswordRequest;
import com.titishop.autenticacion.dto.ValidarCodigoRecuperacionRequest;
import com.titishop.autenticacion.dto.ValidarCodigoRecuperacionResponse;
import com.titishop.autenticacion.entity.PasswordResetCode;
import com.titishop.autenticacion.exception.RecuperacionPasswordInvalidaException;
import com.titishop.autenticacion.repository.PasswordResetCodeRepository;
import com.titishop.usuarios.entity.EstadoUsuario;
import com.titishop.usuarios.entity.Usuario;
import com.titishop.usuarios.repository.UsuarioRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RecuperacionPasswordService {

	private static final String MENSAJE_SOLICITUD = "Si el correo existe, se envió un código de recuperación.";
	private static final String MENSAJE_RESTABLECIDO = "Contraseña actualizada correctamente.";

	private final UsuarioRepository usuarioRepository;
	private final PasswordResetCodeRepository resetCodeRepository;
	private final PasswordEncoder passwordEncoder;
	private final RecuperacionPasswordMailService mailService;
	private final CodigoRecuperacionGenerator generator;
	private final Clock clock;
	private final long expirationMinutes;
	private final int maxAttempts;

	@Autowired
	public RecuperacionPasswordService(
			UsuarioRepository usuarioRepository,
			PasswordResetCodeRepository resetCodeRepository,
			PasswordEncoder passwordEncoder,
			RecuperacionPasswordMailService mailService,
			CodigoRecuperacionGenerator generator,
			Clock clock,
			@Value("${app.password-reset.code-expiration-minutes:10}") long expirationMinutes,
			@Value("${app.password-reset.max-attempts:5}") int maxAttempts
	) {
		this.usuarioRepository = usuarioRepository;
		this.resetCodeRepository = resetCodeRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailService = mailService;
		this.generator = generator;
		this.clock = clock;
		this.expirationMinutes = expirationMinutes;
		this.maxAttempts = maxAttempts;
	}

	public MensajeResponse solicitar(SolicitarRecuperacionPasswordRequest request) {
		String email = normalizarEmail(request.email());
		usuarioRepository.findByEmailIgnoreCase(email)
				.filter(usuario -> usuario.getEstado() == EstadoUsuario.ACTIVO)
				.ifPresent(usuario -> generarYEnviarCodigo(usuario, email));
		return new MensajeResponse(MENSAJE_SOLICITUD);
	}

	public ValidarCodigoRecuperacionResponse validar(ValidarCodigoRecuperacionRequest request) {
		String email = normalizarEmail(request.email());
		PasswordResetCode resetCode = obtenerCodigoVigente(email);
		validarDisponible(resetCode);

		if (!passwordEncoder.matches(request.codigo(), resetCode.getCodigoHash())) {
			resetCode.registrarIntentoFallido();
			resetCodeRepository.save(resetCode);
			throw new RecuperacionPasswordInvalidaException();
		}

		String resetToken = generator.generarResetToken();
		resetCode.marcarValidado(passwordEncoder.encode(resetToken), clock.instant());
		resetCodeRepository.save(resetCode);
		return new ValidarCodigoRecuperacionResponse(resetToken);
	}

	public MensajeResponse restablecer(RestablecerPasswordRequest request) {
		String email = normalizarEmail(request.email());
		PasswordResetCode resetCode = obtenerCodigoVigente(email);
		validarDisponible(resetCode);
		if (resetCode.getValidadoEn() == null
				|| resetCode.getResetTokenHash() == null
				|| !passwordEncoder.matches(request.resetToken(), resetCode.getResetTokenHash())) {
			throw new RecuperacionPasswordInvalidaException();
		}

		Usuario usuario = resetCode.getUsuario();
		usuario.actualizarPassword(passwordEncoder.encode(request.nuevaPassword()));
		resetCode.marcarUsado(clock.instant());
		usuarioRepository.save(usuario);
		resetCodeRepository.save(resetCode);
		return new MensajeResponse(MENSAJE_RESTABLECIDO);
	}

	private void generarYEnviarCodigo(Usuario usuario, String email) {
		String codigo = generator.generarCodigo();
		Instant ahora = clock.instant();
		PasswordResetCode resetCode = new PasswordResetCode(
				usuario,
				passwordEncoder.encode(codigo),
				ahora.plus(expirationMinutes, ChronoUnit.MINUTES),
				ahora
		);
		resetCodeRepository.save(resetCode);
		mailService.enviarCodigo(email, usuario.getNombreCompleto(), codigo);
	}

	private PasswordResetCode obtenerCodigoVigente(String email) {
		return resetCodeRepository.findTopByUsuarioEmailIgnoreCaseAndUsadoEnIsNullOrderByCreadoEnDesc(email)
				.orElseThrow(RecuperacionPasswordInvalidaException::new);
	}

	private void validarDisponible(PasswordResetCode resetCode) {
		if (!resetCode.getExpiraEn().isAfter(clock.instant()) || resetCode.getIntentos() >= maxAttempts) {
			throw new RecuperacionPasswordInvalidaException();
		}
	}

	private String normalizarEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}
}
