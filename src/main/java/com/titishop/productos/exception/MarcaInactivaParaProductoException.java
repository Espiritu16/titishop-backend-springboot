package com.titishop.productos.exception;

import java.util.UUID;

public class MarcaInactivaParaProductoException extends RuntimeException {

	public MarcaInactivaParaProductoException(UUID marcaId) {
		super("La marca no esta activa para productos: " + marcaId);
	}
}
