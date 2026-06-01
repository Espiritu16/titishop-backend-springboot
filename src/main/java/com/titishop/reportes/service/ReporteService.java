package com.titishop.reportes.service;

import com.titishop.inventario.entity.Inventario;
import com.titishop.inventario.repository.InventarioRepository;
import com.titishop.movimientos.entity.Movimiento;
import com.titishop.movimientos.repository.MovimientoRepository;
import com.titishop.productos.entity.Producto;
import com.titishop.proveedores.entity.Proveedor;
import com.titishop.reportes.dto.ReporteMovimientosRequest;
import com.titishop.reportes.dto.ReporteMovimientosResponse;
import com.titishop.reportes.dto.ReporteStockCriticoResponse;
import com.titishop.reportes.dto.ReporteStockResponse;
import com.titishop.reportes.dto.ReporteValorizacionItemResponse;
import com.titishop.reportes.dto.ReporteValorizacionResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReporteService {

	private final MovimientoRepository movimientoRepository;
	private final InventarioRepository inventarioRepository;

	public ReporteService(MovimientoRepository movimientoRepository, InventarioRepository inventarioRepository) {
		this.movimientoRepository = movimientoRepository;
		this.inventarioRepository = inventarioRepository;
	}

	public List<ReporteMovimientosResponse> reporteMovimientos(ReporteMovimientosRequest request) {
		return movimientoRepository.findAll().stream()
				.filter(movimiento -> coincideFecha(movimiento, request.fechaInicio(), request.fechaFin()))
				.filter(movimiento -> coincideProducto(movimiento, request.productoId()))
				.filter(movimiento -> coincideProveedor(movimiento, request.proveedorId()))
				.filter(movimiento -> request.tipo() == null || movimiento.getTipo().name().equals(request.tipo().name()))
				.filter(movimiento -> Boolean.TRUE.equals(request.incluirAnulados()) || !movimiento.estaAnulado())
				.sorted(Comparator.comparing(Movimiento::getCreadoEn).reversed())
				.map(this::toMovimientoResponse)
				.toList();
	}

	public List<ReporteStockResponse> reporteStock(
			com.titishop.inventario.dto.EstadoInventario estado,
			UUID categoriaId,
			UUID marcaId,
			String busqueda
	) {
		String texto = normalizarBusqueda(busqueda);
		return inventarioRepository.findAll().stream()
				.filter(inventario -> estado == null || inventario.getEstado().name().equals(estado.name()))
				.filter(inventario -> categoriaId == null || inventario.getProducto().getCategoria().getId().equals(categoriaId))
				.filter(inventario -> marcaId == null || inventario.getProducto().getMarca().getId().equals(marcaId))
				.filter(inventario -> coincideBusqueda(inventario.getProducto(), texto))
				.sorted(Comparator.comparing(inventario -> inventario.getProducto().getNombre().toLowerCase(Locale.ROOT)))
				.map(this::toStockResponse)
				.toList();
	}

	public List<ReporteStockCriticoResponse> reporteStockCritico() {
		return inventarioRepository.findAll().stream()
				.filter(Inventario::esStockCritico)
				.sorted(Comparator.comparingInt(Inventario::getStockActual))
				.map(this::toStockCriticoResponse)
				.toList();
	}

	public ReporteValorizacionResponse reporteValorizacion() {
		List<ReporteValorizacionItemResponse> items = inventarioRepository.findAll().stream()
				.sorted(Comparator.comparing(inventario -> inventario.getProducto().getNombre().toLowerCase(Locale.ROOT)))
				.map(this::toValorizacionItem)
				.toList();

		BigDecimal valorCostoTotal = items.stream()
				.map(ReporteValorizacionItemResponse::valorCosto)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal valorVentaTotal = items.stream()
				.map(ReporteValorizacionItemResponse::valorVenta)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal margenEstimadoTotal = valorVentaTotal.subtract(valorCostoTotal);

		return new ReporteValorizacionResponse(
				items,
				moneda(valorCostoTotal),
				moneda(valorVentaTotal),
				moneda(margenEstimadoTotal)
		);
	}

	private boolean coincideFecha(Movimiento movimiento, LocalDate fechaInicio, LocalDate fechaFin) {
		LocalDate fecha = movimiento.getCreadoEn().atZone(ZoneId.systemDefault()).toLocalDate();
		if (fechaInicio != null && fecha.isBefore(fechaInicio)) {
			return false;
		}
		return fechaFin == null || !fecha.isAfter(fechaFin);
	}

	private boolean coincideProducto(Movimiento movimiento, UUID productoId) {
		return productoId == null || movimiento.getProducto().getId().equals(productoId);
	}

	private boolean coincideProveedor(Movimiento movimiento, UUID proveedorId) {
		if (proveedorId == null) {
			return true;
		}
		return movimiento.getProveedor() != null && movimiento.getProveedor().getId().equals(proveedorId);
	}

	private String normalizarBusqueda(String busqueda) {
		if (busqueda == null || busqueda.isBlank()) {
			return "";
		}
		return busqueda.trim().toLowerCase(Locale.ROOT);
	}

	private boolean coincideBusqueda(Producto producto, String busqueda) {
		if (busqueda.isEmpty()) {
			return true;
		}
		return producto.getNombre().toLowerCase(Locale.ROOT).contains(busqueda)
				|| producto.getSku().toLowerCase(Locale.ROOT).contains(busqueda);
	}

	private ReporteMovimientosResponse toMovimientoResponse(Movimiento movimiento) {
		Proveedor proveedor = movimiento.getProveedor();
		return new ReporteMovimientosResponse(
				movimiento.getId(),
				movimiento.getCreadoEn(),
				movimiento.getProducto().getId(),
				movimiento.getProducto().getNombre(),
				movimiento.getProducto().getSku(),
				proveedor == null ? null : proveedor.getId(),
				proveedor == null ? null : proveedor.getRazonSocial(),
				com.titishop.movimientos.dto.TipoMovimiento.valueOf(movimiento.getTipo().name()),
				movimiento.getCantidad(),
				movimiento.getStockAntes(),
				movimiento.getStockDespues(),
				movimiento.getMotivo(),
				movimiento.getCreadoPor().getId(),
				movimiento.getCreadoPor().getNombreCompleto(),
				movimiento.estaAnulado()
		);
	}

	private ReporteStockResponse toStockResponse(Inventario inventario) {
		Producto producto = inventario.getProducto();
		return new ReporteStockResponse(
				producto.getId(),
				producto.getNombre(),
				producto.getSku(),
				producto.getCategoria().getId(),
				producto.getCategoria().getNombre(),
				producto.getMarca().getId(),
				producto.getMarca().getNombre(),
				inventario.getStockActual(),
				inventario.getStockMinimo(),
				inventario.getUbicacion(),
				com.titishop.inventario.dto.EstadoInventario.valueOf(inventario.getEstado().name()),
				inventario.esStockCritico()
		);
	}

	private ReporteStockCriticoResponse toStockCriticoResponse(Inventario inventario) {
		Producto producto = inventario.getProducto();
		return new ReporteStockCriticoResponse(
				producto.getId(),
				producto.getNombre(),
				producto.getSku(),
				inventario.getStockActual(),
				inventario.getStockMinimo(),
				Math.max(inventario.getStockMinimo() - inventario.getStockActual(), 0),
				inventario.getUbicacion()
		);
	}

	private ReporteValorizacionItemResponse toValorizacionItem(Inventario inventario) {
		Producto producto = inventario.getProducto();
		BigDecimal stock = BigDecimal.valueOf(inventario.getStockActual());
		BigDecimal valorCosto = producto.getPrecioCompra().multiply(stock);
		BigDecimal valorVenta = producto.getPrecioVenta().multiply(stock);
		BigDecimal margenEstimado = valorVenta.subtract(valorCosto);
		return new ReporteValorizacionItemResponse(
				producto.getId(),
				producto.getNombre(),
				producto.getSku(),
				inventario.getStockActual(),
				moneda(producto.getPrecioCompra()),
				moneda(producto.getPrecioVenta()),
				moneda(valorCosto),
				moneda(valorVenta),
				moneda(margenEstimado)
		);
	}

	private BigDecimal moneda(BigDecimal value) {
		return value.setScale(2, RoundingMode.HALF_UP);
	}
}
