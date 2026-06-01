package com.titishop.reportes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.titishop.inventario.entity.Inventario;
import com.titishop.inventario.repository.InventarioRepository;
import com.titishop.movimientos.entity.Movimiento;
import com.titishop.movimientos.entity.TipoMovimiento;
import com.titishop.movimientos.repository.MovimientoRepository;
import com.titishop.productos.entity.Categoria;
import com.titishop.productos.entity.Marca;
import com.titishop.productos.entity.Producto;
import com.titishop.proveedores.entity.Proveedor;
import com.titishop.reportes.dto.ReporteMovimientosRequest;
import com.titishop.reportes.dto.ReporteMovimientosResponse;
import com.titishop.reportes.dto.ReporteStockCriticoResponse;
import com.titishop.reportes.dto.ReporteStockResponse;
import com.titishop.reportes.dto.ReporteValorizacionResponse;
import com.titishop.reportes.service.ReporteService;
import com.titishop.usuarios.entity.RolUsuario;
import com.titishop.usuarios.entity.Usuario;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTests {

	@Mock
	private MovimientoRepository movimientoRepository;
	@Mock
	private InventarioRepository inventarioRepository;

	private ReporteService reporteService;

	@BeforeEach
	void setUp() {
		reporteService = new ReporteService(movimientoRepository, inventarioRepository);
	}

	@Test
	void reporteMovimientosFiltraPorTipoProveedorYFecha() {
		UUID proveedorId = UUID.randomUUID();
		Proveedor proveedor = proveedorActivo(proveedorId);
		Producto producto = producto(UUID.randomUUID(), "Mouse", "SKU-MOUSE", BigDecimal.valueOf(20), BigDecimal.valueOf(35));
		Usuario usuario = usuarioActivo(UUID.randomUUID());
		Movimiento entrada = movimiento(producto, proveedor, TipoMovimiento.ENTRADA, 5, 10, 15, usuario, "2026-06-01T10:00:00Z");
		Movimiento salida = movimiento(producto, null, TipoMovimiento.SALIDA, 2, 15, 13, usuario, "2026-06-01T11:00:00Z");
		when(movimientoRepository.findAll()).thenReturn(List.of(entrada, salida));
		ReporteMovimientosRequest request = new ReporteMovimientosRequest(
				LocalDate.of(2026, 6, 1),
				LocalDate.of(2026, 6, 1),
				null,
				proveedorId,
				com.titishop.movimientos.dto.TipoMovimiento.ENTRADA,
				false
		);

		List<ReporteMovimientosResponse> result = reporteService.reporteMovimientos(request);

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().tipo()).isEqualTo(com.titishop.movimientos.dto.TipoMovimiento.ENTRADA);
		assertThat(result.getFirst().proveedorId()).isEqualTo(proveedorId);
	}

	@Test
	void reporteStockRetornaInventarioConDatosDeProducto() {
		Producto producto = producto(UUID.randomUUID(), "Mouse", "SKU-MOUSE", BigDecimal.valueOf(20), BigDecimal.valueOf(35));
		Inventario inventario = new Inventario(producto, 8, 5, "A-01");
		when(inventarioRepository.findAll()).thenReturn(List.of(inventario));

		List<ReporteStockResponse> result = reporteService.reporteStock(null, null, null, "mouse");

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().productoSku()).isEqualTo("SKU-MOUSE");
		assertThat(result.getFirst().stockCritico()).isFalse();
	}

	@Test
	void reporteStockCriticoCalculaCantidadSugerida() {
		Producto producto = producto(UUID.randomUUID(), "Mouse", "SKU-MOUSE", BigDecimal.valueOf(20), BigDecimal.valueOf(35));
		Inventario inventario = new Inventario(producto, 3, 5, "A-01");
		when(inventarioRepository.findAll()).thenReturn(List.of(inventario));

		List<ReporteStockCriticoResponse> result = reporteService.reporteStockCritico();

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().cantidadSugerida()).isEqualTo(2);
	}

	@Test
	void reporteValorizacionCalculaTotales() {
		Producto mouse = producto(UUID.randomUUID(), "Mouse", "SKU-MOUSE", BigDecimal.valueOf(20), BigDecimal.valueOf(35));
		Producto teclado = producto(UUID.randomUUID(), "Teclado", "SKU-KEY", BigDecimal.valueOf(50), BigDecimal.valueOf(80));
		when(inventarioRepository.findAll()).thenReturn(List.of(
				new Inventario(mouse, 2, 1, "A-01"),
				new Inventario(teclado, 3, 1, "B-01")
		));

		ReporteValorizacionResponse result = reporteService.reporteValorizacion();

		assertThat(result.valorCostoTotal()).isEqualByComparingTo("190.00");
		assertThat(result.valorVentaTotal()).isEqualByComparingTo("310.00");
		assertThat(result.margenEstimadoTotal()).isEqualByComparingTo("120.00");
	}

	private Movimiento movimiento(
			Producto producto,
			Proveedor proveedor,
			TipoMovimiento tipo,
			Integer cantidad,
			Integer stockAntes,
			Integer stockDespues,
			Usuario usuario,
			String creadoEn
	) {
		Movimiento movimiento = new Movimiento(producto, proveedor, tipo, cantidad, "Motivo", stockAntes, stockDespues, usuario);
		ReflectionTestUtils.setField(movimiento, "id", UUID.randomUUID());
		ReflectionTestUtils.setField(movimiento, "creadoEn", Instant.parse(creadoEn));
		return movimiento;
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

	private Proveedor proveedorActivo(UUID id) {
		Proveedor proveedor = new Proveedor("Proveedor Uno", "20609998881", "987654321", "014700000", "ventas@uno.pe", "Av. Uno 123");
		ReflectionTestUtils.setField(proveedor, "id", id);
		return proveedor;
	}

	private Usuario usuarioActivo(UUID id) {
		Usuario usuario = new Usuario("Admin Uno", "admin@titishop.pe", "hash", RolUsuario.ADMINISTRADOR);
		ReflectionTestUtils.setField(usuario, "id", id);
		return usuario;
	}
}
