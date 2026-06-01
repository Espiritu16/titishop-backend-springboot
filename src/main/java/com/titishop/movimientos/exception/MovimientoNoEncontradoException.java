package com.titishop.movimientos.exception;

import java.util.UUID;

public class MovimientoNoEncontradoException extends RuntimeException {

	public MovimientoNoEncontradoException(UUID id) {
		super("Movimiento no encontrado: " + id);
	}
}
