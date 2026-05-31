package com.titishop.productos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record ActualizarProductoRequest(
		@NotBlank @Size(max = 120) String nombre,
		@NotBlank @Size(max = 40) String sku,
		@NotBlank @Size(max = 2000) String descripcion,
		@Size(max = 500) String imagenUrl,
		@NotNull UUID categoriaId,
		@NotNull UUID marcaId,
		@NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal precioCompra,
		@NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal precioVenta,
		@NotNull EstadoProducto estado
) {
}
