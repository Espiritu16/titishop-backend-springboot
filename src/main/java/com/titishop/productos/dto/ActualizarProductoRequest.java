package com.titishop.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(name = "ActualizarProductoRequest", description = "Payload para actualizar un producto existente.")
public record ActualizarProductoRequest(
		@Schema(description = "Nombre comercial actualizado.", example = "Leche Evaporada Entera 410g", maxLength = 120)
		@NotBlank @Size(max = 120) String nombre,
		@Schema(description = "SKU actualizado del producto.", example = "LEC-410-001", maxLength = 40)
		@NotBlank @Size(max = 40) String sku,
		@Schema(description = "Descripcion actualizada del producto.", example = "Leche evaporada entera en lata de 410 gramos.", maxLength = 2000)
		@NotBlank @Size(max = 2000) String descripcion,
		@Schema(description = "URL de la imagen del producto.", example = "https://cdn.titishop.local/productos/leche-410g.png", maxLength = 500, nullable = true)
		@Size(max = 500) String imagenUrl,
		@Schema(description = "Identificador de la categoria relacionada.", example = "0f1e2d3c-4b5a-6789-9012-3456789abcde")
		@NotNull UUID categoriaId,
		@Schema(description = "Identificador de la marca relacionada.", example = "1ab2cd34-56ef-7890-ab12-cd34ef567890")
		@NotNull UUID marcaId,
		@Schema(description = "Identificador del proveedor principal relacionado.", example = "5a81e2d0-55f8-4a3b-8d65-febec9959002")
		@NotNull UUID proveedorId,
		@Schema(description = "Pais de origen del producto importado.", example = "China", maxLength = 80)
		@NotBlank @Size(max = 80) String paisOrigen,
		@Schema(description = "Precio de compra actual.", example = "3.30", minimum = "0")
		@NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal precioCompra,
		@Schema(description = "Precio de venta actual.", example = "4.80", minimum = "0")
		@NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal precioVenta,
		@Schema(description = "Estado comercial del producto.", example = "ACTIVO")
		@NotNull EstadoProducto estado
) {
}
