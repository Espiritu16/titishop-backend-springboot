package com.titishop;

import static org.assertj.core.api.Assertions.assertThat;

import com.titishop.inventario.entity.Inventario;
import com.titishop.movimientos.entity.Movimiento;
import com.titishop.productos.entity.Categoria;
import com.titishop.productos.entity.Marca;
import com.titishop.productos.entity.Producto;
import com.titishop.proveedores.entity.Proveedor;
import com.titishop.usuarios.entity.Usuario;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class ModeloEntidadesTests {

	@Test
	void entidadesIncluyenCamposPrincipales() throws NoSuchFieldException {
		assertField(Usuario.class, "nombreCompleto", String.class);
		assertField(Usuario.class, "email", String.class);
		assertField(Usuario.class, "passwordHash", String.class);
		assertField(Usuario.class, "rol", Enum.class);
		assertField(Usuario.class, "estado", Enum.class);

		assertField(Categoria.class, "nombre", String.class);
		assertField(Marca.class, "nombre", String.class);

		assertField(Producto.class, "nombre", String.class);
		assertField(Producto.class, "sku", String.class);
		assertField(Producto.class, "descripcion", String.class);
		assertField(Producto.class, "imagenUrl", String.class);
		assertField(Producto.class, "categoria", Categoria.class);
		assertField(Producto.class, "marca", Marca.class);

		assertField(Proveedor.class, "razonSocial", String.class);
		assertField(Proveedor.class, "ruc", String.class);

		assertField(Inventario.class, "producto", Producto.class);
		assertField(Inventario.class, "stockActual", Integer.class);
		assertField(Inventario.class, "stockMinimo", Integer.class);

		assertField(Movimiento.class, "producto", Producto.class);
		assertField(Movimiento.class, "proveedor", Proveedor.class);
		assertField(Movimiento.class, "cantidad", Integer.class);
		assertField(Movimiento.class, "stockAntes", Integer.class);
		assertField(Movimiento.class, "stockDespues", Integer.class);
	}

	@Test
	void entidadesAuditablesIncluyenCamposDeAuditoria() throws NoSuchFieldException {
		for (Class<?> entityClass : new Class<?>[] {
				Usuario.class,
				Categoria.class,
				Marca.class,
				Producto.class,
				Proveedor.class,
				Inventario.class
		}) {
			assertField(entityClass, "creadoPor", Usuario.class);
			assertField(entityClass, "actualizadoPor", Usuario.class);
			assertField(entityClass, "inactivadoPor", Usuario.class);
		}

		assertField(Movimiento.class, "creadoPor", Usuario.class);
		assertField(Movimiento.class, "anuladoPor", Usuario.class);
	}

	private static void assertField(Class<?> owner, String fieldName, Class<?> expectedType) throws NoSuchFieldException {
		Field field = findField(owner, fieldName);

		if (expectedType == Enum.class) {
			assertThat(field.getType().isEnum()).isTrue();
			return;
		}

		assertThat(field.getType()).isEqualTo(expectedType);
	}

	private static Field findField(Class<?> owner, String fieldName) throws NoSuchFieldException {
		Class<?> current = owner;
		while (current != null) {
			try {
				return current.getDeclaredField(fieldName);
			} catch (NoSuchFieldException ignored) {
				current = current.getSuperclass();
			}
		}
		throw new NoSuchFieldException(fieldName);
	}
}
