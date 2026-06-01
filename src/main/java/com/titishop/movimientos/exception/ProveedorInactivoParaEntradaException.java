package com.titishop.movimientos.exception;

import java.util.UUID;

public class ProveedorInactivoParaEntradaException extends RuntimeException {

	public ProveedorInactivoParaEntradaException(UUID proveedorId) {
		super("El proveedor no esta activo para registrar entradas: " + proveedorId);
	}
}
