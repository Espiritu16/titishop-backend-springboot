package com.titishop.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(name = "CrearProductoRequest", description = "Payload para registrar un producto del catalogo.")
public record CrearProductoRequest(
		@Schema(description = "Nombre comercial del producto.", example = "Leche Evaporada Entera 400g", maxLength = 120)
		@NotBlank @Size(max = 120) String nombre,
		@Schema(description = "SKU unico para control interno.", example = "LEC-400-001", maxLength = 40)
		@NotBlank @Size(max = 40) String sku,
		@Schema(description = "Descripcion visible del producto.", example = "Leche evaporada entera en lata de 400 gramos.", maxLength = 2000)
		@NotBlank @Size(max = 2000) String descripcion,
		@Schema(description = "URL publica de la imagen del producto.", example = "https://cdn.titishop.local/productos/leche-400g.png", maxLength = 500, nullable = true)
		@Size(max = 500) String imagenUrl,
		@Schema(description = "Identificador de la categoria.", example = "0f1e2d3c-4b5a-6789-9012-3456789abcde")
		@NotNull UUID categoriaId,
		@Schema(description = "Identificador de la marca.", example = "1ab2cd34-56ef-7890-ab12-cd34ef567890")
		@NotNull UUID marcaId,
		@Schema(description = "Precio de compra del producto.", example = "3.20", minimum = "0")
		@NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal precioCompra,
		@Schema(description = "Precio de venta del producto.", example = "4.50", minimum = "0")
		@NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal precioVenta
) {
}
