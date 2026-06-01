package com.titishop.movimientos.exception;

import java.util.UUID;

public class MovimientoYaAnuladoException extends RuntimeException {

	public MovimientoYaAnuladoException(UUID id) {
		super("El movimiento ya fue anulado: " + id);
	}
}
