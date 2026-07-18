package com.titishop.productos.exception;

import java.util.UUID;

public class CategoriaInactivaParaProductoException extends RuntimeException {

	public CategoriaInactivaParaProductoException(UUID categoriaId) {
		super("La categoría no está activa para productos: " + categoriaId);
	}
}
