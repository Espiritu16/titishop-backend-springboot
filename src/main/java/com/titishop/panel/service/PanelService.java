package com.titishop.panel.service;

import com.titishop.inventario.entity.Inventario;
import com.titishop.inventario.repository.InventarioRepository;
import com.titishop.movimientos.entity.Movimiento;
import com.titishop.movimientos.entity.TipoMovimiento;
import com.titishop.movimientos.repository.MovimientoRepository;
import com.titishop.panel.dto.PanelResumenResponse;
import com.titishop.panel.dto.PanelUltimoMovimientoResponse;
import com.titishop.productos.entity.EstadoProducto;
import com.titishop.productos.entity.Producto;
import com.titishop.productos.repository.ProductoRepository;
import com.titishop.proveedores.entity.EstadoProveedor;
import com.titishop.proveedores.entity.Proveedor;
import com.titishop.proveedores.repository.ProveedorRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PanelService {

	private static final int LIMITE_ULTIMOS_MOVIMIENTOS = 5;

	private final ProductoRepository productoRepository;
	private final ProveedorRepository proveedorRepository;
	private final InventarioRepository inventarioRepository;
	private final MovimientoRepository movimientoRepository;

	public PanelService(
			ProductoRepository productoRepository,
			ProveedorRepository proveedorRepository,
			InventarioRepository inventarioRepository,
			MovimientoRepository movimientoRepository
	) {
		this.productoRepository = productoRepository;
		this.proveedorRepository = proveedorRepository;
		this.inventarioRepository = inventarioRepository;
		this.movimientoRepository = movimientoRepository;
	}

	public PanelResumenResponse resumen() {
		List<Producto> productos = productoRepository.findAll();
		List<Proveedor> proveedores = proveedorRepository.findAll();
		List<Inventario> inventarios = inventarioRepository.findAll();
		List<Movimiento> movimientos = movimientoRepository.findAll();

		LocalDate hoy = LocalDate.now();
		return new PanelResumenResponse(
				contarProductosActivos(productos),
				contarProveedoresActivos(proveedores),
				contarStockCritico(inventarios),
				contarMovimientosDelDia(movimientos, hoy),
				contarMovimientosDelMes(movimientos, hoy, TipoMovimiento.ENTRADA),
				contarMovimientosDelMes(movimientos, hoy, TipoMovimiento.SALIDA),
				valorEstimadoInventario(inventarios),
				ultimosMovimientos(movimientos)
		);
	}

	private int contarProductosActivos(List<Producto> productos) {
		return (int) productos.stream()
				.filter(producto -> producto.getEstado() == EstadoProducto.ACTIVO)
				.count();
	}

	private int contarProveedoresActivos(List<Proveedor> proveedores) {
		return (int) proveedores.stream()
				.filter(proveedor -> proveedor.getEstado() == EstadoProveedor.ACTIVO)
				.count();
	}

	private int contarStockCritico(List<Inventario> inventarios) {
		return (int) inventarios.stream()
				.filter(Inventario::esStockCritico)
				.count();
	}

	private int contarMovimientosDelDia(List<Movimiento> movimientos, LocalDate hoy) {
		return (int) movimientos.stream()
				.filter(movimiento -> fechaMovimiento(movimiento).isEqual(hoy))
				.count();
	}

	private int contarMovimientosDelMes(List<Movimiento> movimientos, LocalDate hoy, TipoMovimiento tipo) {
		return (int) movimientos.stream()
				.filter(movimiento -> movimiento.getTipo() == tipo)
				.filter(movimiento -> {
					LocalDate fecha = fechaMovimiento(movimiento);
					return fecha.getYear() == hoy.getYear() && fecha.getMonth() == hoy.getMonth();
				})
				.count();
	}

	private BigDecimal valorEstimadoInventario(List<Inventario> inventarios) {
		BigDecimal total = inventarios.stream()
				.map(inventario -> inventario.getProducto().getPrecioVenta()
						.multiply(BigDecimal.valueOf(inventario.getStockActual())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		return total.setScale(2, RoundingMode.HALF_UP);
	}

	private List<PanelUltimoMovimientoResponse> ultimosMovimientos(List<Movimiento> movimientos) {
		return movimientos.stream()
				.sorted(Comparator.comparing(Movimiento::getCreadoEn).reversed())
				.limit(LIMITE_ULTIMOS_MOVIMIENTOS)
				.map(this::toUltimoMovimiento)
				.toList();
	}

	private PanelUltimoMovimientoResponse toUltimoMovimiento(Movimiento movimiento) {
		return new PanelUltimoMovimientoResponse(
				movimiento.getId(),
				movimiento.getCreadoEn(),
				movimiento.getProducto().getNombre(),
				movimiento.getProducto().getSku(),
				com.titishop.movimientos.dto.TipoMovimiento.valueOf(movimiento.getTipo().name()),
				movimiento.getCantidad(),
				movimiento.getCreadoPor().getNombreCompleto()
		);
	}

	private LocalDate fechaMovimiento(Movimiento movimiento) {
		return movimiento.getCreadoEn().atZone(ZoneId.systemDefault()).toLocalDate();
	}
}
