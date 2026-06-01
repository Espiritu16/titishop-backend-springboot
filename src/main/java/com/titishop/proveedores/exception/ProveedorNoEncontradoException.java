package com.titishop.proveedores.exception;

import java.util.UUID;

public class ProveedorNoEncontradoException extends RuntimeException {

	public ProveedorNoEncontradoException(UUID id) {
		super("Proveedor no encontrado: " + id);
	}
}
