package com.titishop.inventario.exception;

import java.util.UUID;

public class InventarioDuplicadoPorProductoException extends RuntimeException {

	public InventarioDuplicadoPorProductoException(UUID productoId) {
		super("El producto ya tiene inventario registrado: " + productoId);
	}
}
