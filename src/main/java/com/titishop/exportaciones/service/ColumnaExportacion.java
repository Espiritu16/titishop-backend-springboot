package com.titishop.exportaciones.service;

import java.util.function.Function;

public record ColumnaExportacion<T>(
		String encabezado,
		Function<T, Object> valor
) {
}
