package com.titishop.autenticacion.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	private final JwtEncoder jwtEncoder;

	private final JwtProperties jwtProperties;

	public JwtService(JwtEncoder jwtEncoder, JwtProperties jwtProperties) {
		this.jwtEncoder = jwtEncoder;
		this.jwtProperties = jwtProperties;
	}

	public String generarToken(UUID usuarioId, String email, String nombreCompleto, String rol, Instant emitidoEn) {
		Instant expiraEn = calcularExpiracion(emitidoEn);
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(jwtProperties.getIssuer())
				.issuedAt(emitidoEn)
				.expiresAt(expiraEn)
				.subject(email)
				.claim("usuarioId", usuarioId.toString())
				.claim("nombreCompleto", nombreCompleto)
				.claim("rol", rol)
				.claim("authorities", List.of("ROLE_" + rol))
				.build();

		JwsHeader headers = JwsHeader.with(MacAlgorithm.HS256).build();
		return jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
	}

	public Instant calcularExpiracion(Instant emitidoEn) {
		return emitidoEn.plus(jwtProperties.getExpirationMinutes(), ChronoUnit.MINUTES);
	}
}
