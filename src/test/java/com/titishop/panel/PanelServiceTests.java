package com.titishop.panel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.titishop.inventario.entity.Inventario;
import com.titishop.inventario.repository.InventarioRepository;
import com.titishop.movimientos.entity.Movimiento;
import com.titishop.movimientos.entity.TipoMovimiento;
import com.titishop.movimientos.repository.MovimientoRepository;
import com.titishop.panel.dto.PanelResumenResponse;
import com.titishop.panel.service.PanelService;
import com.titishop.productos.entity.Categoria;
import com.titishop.productos.entity.EstadoProducto;
import com.titishop.productos.entity.Marca;
import com.titishop.productos.entity.Producto;
import com.titishop.productos.repository.ProductoRepository;
import com.titishop.proveedores.entity.EstadoProveedor;
import com.titishop.proveedores.entity.Proveedor;
import com.titishop.proveedores.repository.ProveedorRepository;
import com.titishop.usuarios.entity.RolUsuario;
import com.titishop.usuarios.entity.Usuario;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PanelServiceTests {

	@Mock
	private ProductoRepository productoRepository;
	@Mock
	private ProveedorRepository proveedorRepository;
	@Mock
	private InventarioRepository inventarioRepository;
	@Mock
	private MovimientoRepository movimientoRepository;

	private PanelService panelService;

	@BeforeEach
	void setUp() {
		panelService = new PanelService(productoRepository, proveedorRepository, inventarioRepository, movimientoRepository);
	}

	@Test
	void resumenCalculaIndicadoresOperativos() {
		Producto mouse = producto(UUID.randomUUID(), "Mouse", "SKU-MOUSE", BigDecimal.valueOf(20), BigDecimal.valueOf(35));
		Producto teclado = producto(UUID.randomUUID(), "Teclado", "SKU-KEY", BigDecimal.valueOf(50), BigDecimal.valueOf(80));
		Producto inactivo = producto(UUID.randomUUID(), "Cable", "SKU-CABLE", BigDecimal.ONE, BigDecimal.TEN);
		ReflectionTestUtils.setField(inactivo, "estado", EstadoProducto.INACTIVO);
		Proveedor proveedorActivo = proveedor(UUID.randomUUID(), EstadoProveedor.ACTIVO);
		Proveedor proveedorInactivo = proveedor(UUID.randomUUID(), EstadoProveedor.INACTIVO);
		Usuario usuario = usuarioActivo(UUID.randomUUID());
		LocalDate hoy = LocalDate.now();
		Movimiento entradaMes = movimiento(mouse, proveedorActivo, TipoMovimiento.ENTRADA, 5, usuario, hoy.atTime(10, 0).atZone(ZoneId.systemDefault()).toInstant());
		Movimiento salidaMes = movimiento(teclado, null, TipoMovimiento.SALIDA, 2, usuario, hoy.atTime(11, 0).atZone(ZoneId.systemDefault()).toInstant());
		Movimiento entradaAnterior = movimiento(mouse, proveedorActivo, TipoMovimiento.ENTRADA, 1, usuario, hoy.minusMonths(1).atTime(9, 0).atZone(ZoneId.systemDefault()).toInstant());

		when(productoRepository.findAll()).thenReturn(List.of(mouse, teclado, inactivo));
		when(proveedorRepository.findAll()).thenReturn(List.of(proveedorActivo, proveedorInactivo));
		when(inventarioRepository.findAll()).thenReturn(List.of(
				new Inventario(mouse, 3, 5, "A-01"),
				new Inventario(teclado, 4, 2, "B-01")
		));
		when(movimientoRepository.findAll()).thenReturn(List.of(entradaAnterior, salidaMes, entradaMes));

		PanelResumenResponse resumen = panelService.resumen();

		assertThat(resumen.totalProductosActivos()).isEqualTo(2);
		assertThat(resumen.totalProveedoresActivos()).isEqualTo(1);
		assertThat(resumen.productosStockCritico()).isEqualTo(1);
		assertThat(resumen.movimientosDelDia()).isEqualTo(2);
		assertThat(resumen.entradasDelMes()).isEqualTo(1);
		assertThat(resumen.salidasDelMes()).isEqualTo(1);
		assertThat(resumen.valorEstimadoInventario()).isEqualByComparingTo("425.00");
		assertThat(resumen.ultimosMovimientos()).hasSize(3);
		assertThat(resumen.ultimosMovimientos().getFirst().tipo()).isEqualTo(com.titishop.movimientos.dto.TipoMovimiento.SALIDA);
	}

	private Producto producto(UUID id, String nombre, String sku, BigDecimal precioCompra, BigDecimal precioVenta) {
		Categoria categoria = new Categoria("Perifericos");
		Marca marca = new Marca("Logi");
		ReflectionTestUtils.setField(categoria, "id", UUID.randomUUID());
		ReflectionTestUtils.setField(marca, "id", UUID.randomUUID());
		Producto producto = new Producto(nombre, sku, "Descripcion", null, categoria, marca, precioCompra, precioVenta);
		ReflectionTestUtils.setField(producto, "id", id);
		return producto;
	}

	private Proveedor proveedor(UUID id, EstadoProveedor estado) {
		Proveedor proveedor = new Proveedor("Proveedor Uno", "20609998881", "987654321", "014700000", "ventas@uno.pe", "Av. Uno 123");
		ReflectionTestUtils.setField(proveedor, "id", id);
		ReflectionTestUtils.setField(proveedor, "estado", estado);
		return proveedor;
	}

	private Usuario usuarioActivo(UUID id) {
		Usuario usuario = new Usuario("Admin Uno", "admin@titishop.pe", "hash", RolUsuario.ADMINISTRADOR);
		ReflectionTestUtils.setField(usuario, "id", id);
		return usuario;
	}

	private Movimiento movimiento(Producto producto, Proveedor proveedor, TipoMovimiento tipo, Integer cantidad, Usuario usuario, Instant creadoEn) {
		Movimiento movimiento = new Movimiento(producto, proveedor, tipo, cantidad, "Motivo", 10, 10 + cantidad, usuario);
		ReflectionTestUtils.setField(movimiento, "id", UUID.randomUUID());
		ReflectionTestUtils.setField(movimiento, "creadoEn", creadoEn);
		return movimiento;
	}
}
