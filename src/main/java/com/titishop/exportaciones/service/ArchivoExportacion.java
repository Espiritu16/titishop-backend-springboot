package com.titishop.exportaciones.service;

public record ArchivoExportacion(
		String nombreArchivo,
		String mediaType,
		byte[] contenido
) {
}
