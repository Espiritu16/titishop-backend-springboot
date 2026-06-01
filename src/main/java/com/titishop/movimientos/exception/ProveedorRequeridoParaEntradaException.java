package com.titishop.movimientos.exception;

public class ProveedorRequeridoParaEntradaException extends RuntimeException {

	public ProveedorRequeridoParaEntradaException() {
		super("El proveedor es obligatorio para registrar una entrada.");
	}
}
