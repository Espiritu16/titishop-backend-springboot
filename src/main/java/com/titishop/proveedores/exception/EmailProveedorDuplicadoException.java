package com.titishop.proveedores.exception;

public class EmailProveedorDuplicadoException extends RuntimeException {

	public EmailProveedorDuplicadoException(String email) {
		super("El email del proveedor ya se encuentra registrado: " + email);
	}
}
