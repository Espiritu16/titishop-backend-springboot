package com.titishop.autenticacion.service;

import com.titishop.autenticacion.dto.LoginRequest;
import com.titishop.autenticacion.dto.LoginResponse;
import com.titishop.autenticacion.security.JwtService;
import com.titishop.usuarios.entity.Usuario;
import com.titishop.usuarios.repository.UsuarioRepository;
import java.time.Instant;
import java.util.Locale;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AutenticacionService {

	private final AuthenticationManager authenticationManager;

	private final JwtService jwtService;

	private final UsuarioRepository usuarioRepository;

	public AutenticacionService(
			AuthenticationManager authenticationManager,
			JwtService jwtService,
			UsuarioRepository usuarioRepository
	) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.usuarioRepository = usuarioRepository;
	}

	public LoginResponse login(LoginRequest request) {
		String email = request.email().trim().toLowerCase(Locale.ROOT);
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));

		Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
				.orElseThrow();
		Instant emitidoEn = Instant.now();
		String rol = usuario.getRol().name();
		String token = jwtService.generarToken(usuario.getId(), usuario.getEmail(), usuario.getNombreCompleto(), rol, emitidoEn);

		return new LoginResponse(
				token,
				"Bearer",
				jwtService.calcularExpiracion(emitidoEn),
				usuario.getNombreCompleto(),
				usuario.getEmail(),
				rol
		);
	}
}
