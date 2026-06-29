package com.titishop.productos;

import static org.assertj.core.api.Assertions.assertThat;

import com.titishop.productos.entity.Producto;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class ProductoEstructuraTests {

	@Test
	void productoIncluyeDescripcionEImagen() throws NoSuchFieldException {
		Field descripcion = Producto.class.getDeclaredField("descripcion");
		Field imagenUrl = Producto.class.getDeclaredField("imagenUrl");

		assertThat(descripcion.getType()).isEqualTo(String.class);
		assertThat(imagenUrl.getType()).isEqualTo(String.class);
	}

	@Test
	void productoIncluyePaisOrigenYProveedor() throws NoSuchFieldException {
		Field paisOrigen = Producto.class.getDeclaredField("paisOrigen");
		Field proveedor = Producto.class.getDeclaredField("proveedor");

		assertThat(paisOrigen.getType()).isEqualTo(String.class);
		assertThat(proveedor.getType().getSimpleName()).isEqualTo("Proveedor");
	}
}
