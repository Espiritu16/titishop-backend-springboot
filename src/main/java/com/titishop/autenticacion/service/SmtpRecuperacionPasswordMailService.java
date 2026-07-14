package com.titishop.autenticacion.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpRecuperacionPasswordMailService implements RecuperacionPasswordMailService {

	private final JavaMailSender mailSender;
	private final String from;

	public SmtpRecuperacionPasswordMailService(
			JavaMailSender mailSender,
			@Value("${app.mail.from:no-reply@titishop.local}") String from
	) {
		this.mailSender = mailSender;
		this.from = from;
	}

	@Override
	public void enviarCodigo(String email, String nombreCompleto, String codigo) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(email);
		message.setSubject("Codigo de recuperacion de TitiShop");
		message.setText("""
				Hola %s,

				Tu codigo para recuperar la contrasena de TitiShop es: %s

				Este codigo vence en 10 minutos. Si no solicitaste este cambio, ignora este correo.
				""".formatted(nombreCompleto, codigo));
		mailSender.send(message);
	}
}
