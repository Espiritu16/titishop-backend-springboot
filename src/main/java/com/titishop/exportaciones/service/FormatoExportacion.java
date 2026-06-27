package com.titishop.exportaciones.service;

import com.titishop.exportaciones.exception.FormatoExportacionInvalidoException;
import java.util.Locale;

public enum FormatoExportacion {
	EXCEL("application/vnd.ms-excel", "xls"),
	PDF("application/pdf", "pdf");

	private final String mediaType;
	private final String extension;

	FormatoExportacion(String mediaType, String extension) {
		this.mediaType = mediaType;
		this.extension = extension;
	}

	public String mediaType() {
		return mediaType;
	}

	public String extension() {
		return extension;
	}

	public static FormatoExportacion desde(String value) {
		if (value == null) {
			throw new FormatoExportacionInvalidoException();
		}
		try {
			return FormatoExportacion.valueOf(value.trim().toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException ex) {
			throw new FormatoExportacionInvalidoException();
		}
	}
}
