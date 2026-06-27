package com.titishop.exportaciones.exception;

public class ExportacionSinDatosException extends RuntimeException {

	public ExportacionSinDatosException() {
		super("No hay datos para exportar con los filtros seleccionados.");
	}
}
