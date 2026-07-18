package com.titishop.autenticacion.exception;

public class RecuperacionPasswordInvalidaException extends RuntimeException {

	public RecuperacionPasswordInvalidaException() {
		super("El código o token de recuperación no es válido.");
	}
}
