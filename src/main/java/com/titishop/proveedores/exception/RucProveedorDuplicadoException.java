package com.titishop.proveedores.exception;

public class RucProveedorDuplicadoException extends RuntimeException {

	public RucProveedorDuplicadoException(String ruc) {
		super("El RUC del proveedor ya se encuentra registrado: " + ruc);
	}
}
