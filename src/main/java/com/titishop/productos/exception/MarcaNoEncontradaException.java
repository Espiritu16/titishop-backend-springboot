package com.titishop.productos.exception;

import java.util.UUID;

public class MarcaNoEncontradaException extends RuntimeException {

	public MarcaNoEncontradaException(UUID id) {
		super("Marca no encontrada: " + id);
	}
}
