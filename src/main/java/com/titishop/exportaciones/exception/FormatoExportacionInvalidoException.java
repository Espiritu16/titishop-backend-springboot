package com.titishop.exportaciones.exception;

public class FormatoExportacionInvalidoException extends RuntimeException {

	public FormatoExportacionInvalidoException() {
		super("Formato de exportacion invalido. Use excel o pdf.");
	}
}
