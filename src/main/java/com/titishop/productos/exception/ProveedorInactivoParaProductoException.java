package com.titishop.productos.exception;

import java.util.UUID;

public class ProveedorInactivoParaProductoException extends RuntimeException {

	public ProveedorInactivoParaProductoException(UUID proveedorId) {
		super("El proveedor no esta activo para asociarlo al producto: " + proveedorId);
	}
}
