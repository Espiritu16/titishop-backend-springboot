package com.titishop.proveedores.exception;

public class FactilizaServicioNoDisponibleException extends RuntimeException {

	public FactilizaServicioNoDisponibleException() {
		super("No se pudo consultar Factiliza en este momento.");
	}

	public FactilizaServicioNoDisponibleException(Throwable cause) {
		super("No se pudo consultar Factiliza en este momento.", cause);
	}
}
