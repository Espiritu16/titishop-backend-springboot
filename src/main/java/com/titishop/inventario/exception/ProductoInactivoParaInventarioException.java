package com.titishop.inventario.exception;

import java.util.UUID;

public class ProductoInactivoParaInventarioException extends RuntimeException {

	public ProductoInactivoParaInventarioException(UUID productoId) {
		super("El producto no esta activo para operar inventario: " + productoId);
	}
}
