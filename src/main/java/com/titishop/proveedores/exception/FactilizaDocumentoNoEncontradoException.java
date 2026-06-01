package com.titishop.proveedores.exception;

public class FactilizaDocumentoNoEncontradoException extends RuntimeException {

	public FactilizaDocumentoNoEncontradoException() {
		super("No se encontraron datos para el RUC ingresado.");
	}
}
