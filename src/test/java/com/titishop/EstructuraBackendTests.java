package com.titishop;

import static org.assertj.core.api.Assertions.assertThat;

import com.titishop.autenticacion.controller.AutenticacionController;
import com.titishop.autenticacion.service.AutenticacionService;
import com.titishop.inventario.controller.InventarioController;
import com.titishop.movimientos.controller.MovimientoController;
import com.titishop.panel.controller.PanelController;
import com.titishop.productos.controller.ProductoController;
import com.titishop.proveedores.controller.ProveedorController;
import com.titishop.reportes.controller.ReporteController;
import com.titishop.usuarios.controller.UsuarioController;
import org.junit.jupiter.api.Test;

class EstructuraBackendTests {

	@Test
	void modulosPrincipalesExistenConControllersYServices() {
		assertThat(AutenticacionController.class).isNotNull();
		assertThat(AutenticacionService.class).isNotNull();
		assertThat(UsuarioController.class).isNotNull();
		assertThat(ProductoController.class).isNotNull();
		assertThat(ProveedorController.class).isNotNull();
		assertThat(InventarioController.class).isNotNull();
		assertThat(MovimientoController.class).isNotNull();
		assertThat(ReporteController.class).isNotNull();
		assertThat(PanelController.class).isNotNull();
	}
}
