package com.titishop.archivos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ArchivoResponse", description = "Archivo guardado en almacenamiento local.")
public record ArchivoResponse(
		@Schema(description = "URL publica para mostrar el archivo.", example = "https://api-titishop.proyectoutp.com/uploads/productos/abc.webp")
		String url,
		@Schema(description = "Ruta relativa dentro del almacenamiento.", example = "productos/abc.webp")
		String ruta,
		@Schema(description = "Nombre original enviado por el cliente.", example = "producto.webp")
		String nombreOriginal,
		@Schema(description = "Tipo MIME validado.", example = "image/webp")
		String contentType,
		@Schema(description = "Tamano del archivo en bytes.", example = "120304")
		long size
) {
}
