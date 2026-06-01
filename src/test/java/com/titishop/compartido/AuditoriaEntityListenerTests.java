package com.titishop.compartido;

import static org.assertj.core.api.Assertions.assertThat;

import com.titishop.compartido.entity.AuditoriaEntityListener;
import com.titishop.productos.entity.Marca;
import com.titishop.usuarios.entity.RolUsuario;
import com.titishop.usuarios.entity.Usuario;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AuditoriaEntityListenerTests {

	@Test
	void alCrearAsignaUsuarioActualComoCreador() {
		Usuario usuario = new Usuario("Admin", "admin@titishop.pe", "hash", RolUsuario.ADMINISTRADOR);
		Marca marca = new Marca("Nike");

		new AuditoriaEntityListener(() -> Optional.of(usuario)).antesCrear(marca);

		assertThat(marca.getCreadoPor()).isSameAs(usuario);
	}

	@Test
	void alActualizarAsignaUsuarioActualComoUltimoEditor() {
		Usuario usuario = new Usuario("Almacen", "almacen@titishop.pe", "hash", RolUsuario.ALMACENERO);
		Marca marca = new Marca("Adidas");

		new AuditoriaEntityListener(() -> Optional.of(usuario)).antesActualizar(marca);

		assertThat(marca.getActualizadoPor()).isSameAs(usuario);
	}

	@Test
	void alInactivarAsignaUsuarioActualComoResponsable() {
		Usuario usuario = new Usuario("Almacen", "almacen@titishop.pe", "hash", RolUsuario.ALMACENERO);
		Marca marca = new Marca("Puma");
		marca.inactivar();

		new AuditoriaEntityListener(() -> Optional.of(usuario)).antesActualizar(marca);

		assertThat(marca.getInactivadoPor()).isSameAs(usuario);
	}
}
