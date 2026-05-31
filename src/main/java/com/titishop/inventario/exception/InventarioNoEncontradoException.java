package com.titishop.inventario.exception;

import java.util.UUID;

public class InventarioNoEncontradoException extends RuntimeException {

	public InventarioNoEncontradoException(UUID id) {
		super("Inventario no encontrado: " + id);
	}
}
