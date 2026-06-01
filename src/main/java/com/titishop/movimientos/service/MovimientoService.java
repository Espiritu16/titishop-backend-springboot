package com.titishop.movimientos.service;

import com.titishop.inventario.entity.EstadoInventario;
import com.titishop.inventario.entity.Inventario;
import com.titishop.inventario.exception.InventarioNoEncontradoException;
import com.titishop.inventario.repository.InventarioRepository;
import com.titishop.movimientos.dto.MovimientoResponse;
import com.titishop.movimientos.dto.RegistrarMovimientoRequest;
import com.titishop.movimientos.repository.MovimientoRepository;
import com.titishop.movimientos.entity.Movimiento;
import com.titishop.movimientos.exception.MovimientoInvalidoException;
import com.titishop.movimientos.exception.MovimientoNoEncontradoException;
import com.titishop.movimientos.exception.MovimientoYaAnuladoException;
import com.titishop.movimientos.exception.ProveedorInactivoParaEntradaException;
import com.titishop.movimientos.exception.ProveedorRequeridoParaEntradaException;
import com.titishop.movimientos.exception.StockInsuficienteException;
import com.titishop.productos.entity.EstadoProducto;
import com.titishop.productos.entity.Producto;
import com.titishop.productos.exception.ProductoNoEncontradoException;
import com.titishop.productos.repository.ProductoRepository;
import com.titishop.proveedores.entity.EstadoProveedor;
import com.titishop.proveedores.entity.Proveedor;
import com.titishop.proveedores.exception.ProveedorNoEncontradoException;
import com.titishop.proveedores.repository.ProveedorRepository;
import com.titishop.usuarios.entity.Usuario;
import com.titishop.usuarios.exception.UsuarioNoEncontradoException;
import com.titishop.usuarios.repository.UsuarioRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MovimientoService {

	private final MovimientoRepository movimientoRepository;
	private final ProductoRepository productoRepository;
	private final InventarioRepository inventarioRepository;
	private final ProveedorRepository proveedorRepository;
	private final UsuarioRepository usuarioRepository;

	public MovimientoService(
			MovimientoRepository movimientoRepository,
			ProductoRepository productoRepository,
			InventarioRepository inventarioRepository,
			ProveedorRepository proveedorRepository,
			UsuarioRepository usuarioRepository
	) {
		this.movimientoRepository = movimientoRepository;
		this.productoRepository = productoRepository;
		this.inventarioRepository = inventarioRepository;
		this.proveedorRepository = proveedorRepository;
		this.usuarioRepository = usuarioRepository;
	}

	@Transactional(readOnly = true)
	public List<MovimientoResponse> listar() {
		return movimientoRepository.findAll().stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public MovimientoResponse obtenerPorId(UUID id) {
		return toResponse(buscarPorId(id));
	}

	public MovimientoResponse registrar(RegistrarMovimientoRequest request) {
		Producto producto = buscarProductoActivo(request.productoId());
		Inventario inventario = buscarInventarioActivo(request.productoId());
		Proveedor proveedor = resolverProveedor(request);

		int stockAntes = inventario.getStockActual();
		int stockDespues = calcularStockDespues(request, stockAntes);
		int cantidad = calcularCantidad(request, stockAntes, stockDespues);
		Usuario usuario = buscarUsuario(request.usuarioId());

		Movimiento movimiento = new Movimiento(
				producto,
				proveedor,
				com.titishop.movimientos.entity.TipoMovimiento.valueOf(request.tipo().name()),
				cantidad,
				request.motivo().trim(),
				stockAntes,
				stockDespues,
				usuario
		);

		inventario.cambiarStockActual(stockDespues);
		inventarioRepository.save(inventario);
		return toResponse(movimientoRepository.save(movimiento));
	}

	public void anular(UUID id, UUID usuarioId, String motivoAnulacion) {
		Movimiento movimiento = buscarPorId(id);
		if (movimiento.estaAnulado()) {
			throw new MovimientoYaAnuladoException(id);
		}
		Usuario usuario = buscarUsuario(usuarioId);
		Inventario inventario = buscarInventarioActivo(movimiento.getProducto().getId());
		int stockRevertido = calcularStockRevertido(movimiento, inventario.getStockActual());

		inventario.cambiarStockActual(stockRevertido);
		movimiento.anular(usuario, motivoAnulacion.trim());

		inventarioRepository.save(inventario);
		movimientoRepository.save(movimiento);
	}

	private Movimiento buscarPorId(UUID id) {
		return movimientoRepository.findById(id)
				.orElseThrow(() -> new MovimientoNoEncontradoException(id));
	}

	private Producto buscarProductoActivo(UUID productoId) {
		Producto producto = productoRepository.findById(productoId)
				.orElseThrow(() -> new ProductoNoEncontradoException(productoId));
		if (producto.getEstado() != EstadoProducto.ACTIVO) {
			throw new MovimientoInvalidoException("El producto no esta activo para movimientos: " + productoId);
		}
		return producto;
	}

	private Inventario buscarInventarioActivo(UUID productoId) {
		Inventario inventario = inventarioRepository.findByProductoId(productoId)
				.orElseThrow(() -> new InventarioNoEncontradoException(productoId));
		if (inventario.getEstado() != EstadoInventario.ACTIVO) {
			throw new MovimientoInvalidoException("El inventario no esta activo para movimientos: " + productoId);
		}
		return inventario;
	}

	private Proveedor resolverProveedor(RegistrarMovimientoRequest request) {
		if (request.tipo() != com.titishop.movimientos.dto.TipoMovimiento.ENTRADA) {
			return null;
		}
		if (request.proveedorId() == null) {
			throw new ProveedorRequeridoParaEntradaException();
		}
		Proveedor proveedor = proveedorRepository.findById(request.proveedorId())
				.orElseThrow(() -> new ProveedorNoEncontradoException(request.proveedorId()));
		if (proveedor.getEstado() != EstadoProveedor.ACTIVO) {
			throw new ProveedorInactivoParaEntradaException(request.proveedorId());
		}
		return proveedor;
	}

	private Usuario buscarUsuario(UUID usuarioId) {
		return usuarioRepository.findById(usuarioId)
				.orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));
	}

	private int calcularStockDespues(RegistrarMovimientoRequest request, int stockAntes) {
		return switch (request.tipo()) {
			case ENTRADA -> stockAntes + cantidadRequerida(request);
			case SALIDA -> calcularSalida(stockAntes, cantidadRequerida(request));
			case AJUSTE -> stockDestinoRequerido(request);
		};
	}

	private int calcularSalida(int stockAntes, int cantidad) {
		if (stockAntes < cantidad) {
			throw new StockInsuficienteException(stockAntes, cantidad);
		}
		return stockAntes - cantidad;
	}

	private int calcularCantidad(RegistrarMovimientoRequest request, int stockAntes, int stockDespues) {
		if (request.tipo() == com.titishop.movimientos.dto.TipoMovimiento.AJUSTE) {
			int cantidad = Math.abs(stockDespues - stockAntes);
			if (cantidad == 0) {
				throw new MovimientoInvalidoException("El ajuste debe modificar el stock actual.");
			}
			return cantidad;
		}
		return cantidadRequerida(request);
	}

	private int cantidadRequerida(RegistrarMovimientoRequest request) {
		if (request.cantidad() == null || request.cantidad() <= 0) {
			throw new MovimientoInvalidoException("La cantidad debe ser mayor que cero.");
		}
		return request.cantidad();
	}

	private int stockDestinoRequerido(RegistrarMovimientoRequest request) {
		if (request.stockDestino() == null || request.stockDestino() < 0) {
			throw new MovimientoInvalidoException("El stock destino es obligatorio y no puede ser negativo para ajustes.");
		}
		return request.stockDestino();
	}

	private int calcularStockRevertido(Movimiento movimiento, int stockActual) {
		return switch (movimiento.getTipo()) {
			case ENTRADA -> {
				if (stockActual < movimiento.getCantidad()) {
					throw new StockInsuficienteException(stockActual, movimiento.getCantidad());
				}
				yield stockActual - movimiento.getCantidad();
			}
			case SALIDA -> stockActual + movimiento.getCantidad();
			case AJUSTE -> movimiento.getStockAntes();
		};
	}

	private MovimientoResponse toResponse(Movimiento movimiento) {
		Proveedor proveedor = movimiento.getProveedor();
		Usuario anuladoPor = movimiento.getAnuladoPor();
		return new MovimientoResponse(
				movimiento.getId(),
				movimiento.getProducto().getId(),
				movimiento.getProducto().getNombre(),
				movimiento.getProducto().getSku(),
				proveedor == null ? null : proveedor.getId(),
				proveedor == null ? null : proveedor.getRazonSocial(),
				com.titishop.movimientos.dto.TipoMovimiento.valueOf(movimiento.getTipo().name()),
				movimiento.getCantidad(),
				movimiento.getMotivo(),
				movimiento.getStockAntes(),
				movimiento.getStockDespues(),
				movimiento.getCreadoPor().getId(),
				movimiento.getCreadoPor().getNombreCompleto(),
				movimiento.getCreadoEn(),
				movimiento.getAnuladoEn(),
				anuladoPor == null ? null : anuladoPor.getId(),
				movimiento.getMotivoAnulacion()
		);
	}
}
