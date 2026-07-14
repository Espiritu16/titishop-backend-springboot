package com.titishop.autenticacion.exception;

public class RecuperacionPasswordInvalidaException extends RuntimeException {

	public RecuperacionPasswordInvalidaException() {
		super("El codigo o token de recuperacion no es valido.");
	}
}
