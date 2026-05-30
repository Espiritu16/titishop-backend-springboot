package com.titishop.autenticacion.security;

import com.titishop.usuarios.entity.EstadoUsuario;
import com.titishop.usuarios.entity.Usuario;
import com.titishop.usuarios.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetallesService implements UserDetailsService {

	private final UsuarioRepository usuarioRepository;

	public UsuarioDetallesService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));

		return User.withUsername(usuario.getEmail())
				.password(usuario.getPasswordHash())
				.roles(usuario.getRol().name())
				.disabled(usuario.getEstado() != EstadoUsuario.ACTIVO)
				.build();
	}
}
