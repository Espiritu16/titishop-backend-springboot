package com.titishop.compartido.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.titishop.usuarios.repository.UsuarioRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class AuditoriaUsuarioActualProviderTests {

	@Mock
	private UsuarioRepository usuarioRepository;

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void obtenerIgnoraAutenticacionAnonima() {
		SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
				"anonymous",
				"anonymousUser",
				List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
		));
		AuditoriaUsuarioActualProvider provider = new AuditoriaUsuarioActualProvider(usuarioRepository);

		assertThat(provider.obtener()).isEmpty();

		verify(usuarioRepository, never()).findByEmailIgnoreCase("anonymousUser");
	}
}
