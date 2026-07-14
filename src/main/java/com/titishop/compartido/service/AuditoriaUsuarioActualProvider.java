package com.titishop.compartido.service;

import com.titishop.usuarios.entity.Usuario;
import com.titishop.usuarios.repository.UsuarioRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaUsuarioActualProvider {

	private final UsuarioRepository usuarioRepository;

	public AuditoriaUsuarioActualProvider(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	public Optional<Usuario> obtener() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null
				|| !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken) {
			return Optional.empty();
		}

		Object principal = authentication.getPrincipal();
		if (principal instanceof Jwt jwt) {
			String usuarioId = jwt.getClaimAsString("usuarioId");
			if (usuarioId != null && !usuarioId.isBlank()) {
				try {
					return usuarioRepository.findById(UUID.fromString(usuarioId));
				} catch (IllegalArgumentException ignored) {
					return Optional.empty();
				}
			}
			return usuarioRepository.findByEmailIgnoreCase(jwt.getSubject());
		}

		return usuarioRepository.findByEmailIgnoreCase(authentication.getName());
	}
}
