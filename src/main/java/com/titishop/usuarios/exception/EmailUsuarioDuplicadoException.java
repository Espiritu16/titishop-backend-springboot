package com.titishop.usuarios.exception;

public class EmailUsuarioDuplicadoException extends RuntimeException {

	public EmailUsuarioDuplicadoException(String email) {
		super("El email ya se encuentra registrado: " + email);
	}
}
