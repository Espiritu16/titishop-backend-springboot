package com.titishop.exportaciones.exception;

public class FormatoExportacionInvalidoException extends RuntimeException {

	public FormatoExportacionInvalidoException() {
		super("Formato de exportación inválido. Use Excel o PDF.");
	}
}
