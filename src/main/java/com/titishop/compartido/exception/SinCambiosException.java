package com.titishop.compartido.exception;

public class SinCambiosException extends RuntimeException {

	public SinCambiosException() {
		super("No hay cambios para actualizar.");
	}
}
