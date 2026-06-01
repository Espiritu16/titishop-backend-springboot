package com.titishop.autenticacion;

import static org.assertj.core.api.Assertions.assertThat;

import com.titishop.autenticacion.security.JwtService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@SpringBootTest
class AutenticacionSecurityTests {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private JwtDecoder jwtDecoder;

	@Test
	void passwordEncoderUsaBCrypt() {
		assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
		assertThat(passwordEncoder.matches("123456", passwordEncoder.encode("123456"))).isTrue();
	}

	@Test
	void jwtGeneradoIncluyeClaimsPrincipales() {
		UUID usuarioId = UUID.randomUUID();
		String token = jwtService.generarToken(usuarioId, "admin@titishop.pe", "Admin TitiShop", "ADMINISTRADOR", Instant.now());

		Jwt jwt = jwtDecoder.decode(token);

		assertThat(jwt.getClaimAsString("usuarioId")).isEqualTo(usuarioId.toString());
		assertThat(jwt.getSubject()).isEqualTo("admin@titishop.pe");
		assertThat(jwt.getClaimAsString("nombreCompleto")).isEqualTo("Admin TitiShop");
		assertThat(jwt.getClaimAsString("rol")).isEqualTo("ADMINISTRADOR");
		assertThat(jwt.getClaimAsStringList("authorities")).containsExactly("ROLE_ADMINISTRADOR");
	}
}
