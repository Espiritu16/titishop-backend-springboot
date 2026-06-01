package com.titishop.movimientos.exception;

public class StockInsuficienteException extends RuntimeException {

	public StockInsuficienteException(Integer stockActual, Integer cantidad) {
		super("Stock insuficiente. Stock actual: " + stockActual + ", cantidad solicitada: " + cantidad);
	}
}
