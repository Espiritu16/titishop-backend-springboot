package com.titishop.inventario.service;

import com.titishop.inventario.dto.ActualizarInventarioRequest;
import com.titishop.inventario.dto.CrearInventarioRequest;
import com.titishop.inventario.dto.InventarioResponse;
import com.titishop.inventario.entity.Inventario;
import com.titishop.inventario.exception.InventarioDuplicadoPorProductoException;
import com.titishop.inventario.exception.InventarioInvalidoException;
import com.titishop.inventario.exception.InventarioNoEncontradoException;
import com.titishop.inventario.exception.ProductoInactivoParaInventarioException;
import com.titishop.inventario.repository.InventarioRepository;
import com.titishop.productos.entity.EstadoProducto;
import com.titishop.productos.entity.Producto;
import com.titishop.productos.exception.ProductoNoEncontradoException;
import com.titishop.productos.repository.ProductoRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventarioService {

	private final InventarioRepository inventarioRepository;
	private final ProductoRepository productoRepository;

	public InventarioService(InventarioRepository inventarioRepository, ProductoRepository productoRepository) {
		this.inventarioRepository = inventarioRepository;
		this.productoRepository = productoRepository;
	}

	@Transactional(readOnly = true)
	public List<InventarioResponse> listar() {
		return inventarioRepository.findAll().stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public InventarioResponse obtenerPorId(UUID id) {
		return toResponse(buscarPorId(id));
	}

	public InventarioResponse crear(CrearInventarioRequest request) {
		validarStockNoNegativo(request.stockActual(), "El stock actual no puede ser negativo.");
		validarStockNoNegativo(request.stockMinimo(), "El stock minimo no puede ser negativo.");
		Producto producto = buscarProductoActivo(request.productoId());

		if (inventarioRepository.existsByProductoId(request.productoId())) {
			throw new InventarioDuplicadoPorProductoException(request.productoId());
		}

		Inventario inventario = new Inventario(
				producto,
				request.stockActual(),
				request.stockMinimo(),
				request.ubicacion().trim()
		);

		return toResponse(inventarioRepository.save(inventario));
	}

	public InventarioResponse actualizar(UUID id, ActualizarInventarioRequest request) {
		validarStockNoNegativo(request.stockMinimo(), "El stock minimo no puede ser negativo.");
		Inventario inventario = buscarPorId(id);
		inventario.actualizar(
				request.stockMinimo(),
				request.ubicacion().trim(),
				com.titishop.inventario.entity.EstadoInventario.valueOf(request.estado().name())
		);
		return toResponse(inventarioRepository.save(inventario));
	}

	public void inactivar(UUID id) {
		Inventario inventario = buscarPorId(id);
		if (inventario.getStockActual() > 0) {
			throw new InventarioInvalidoException("No se puede inactivar inventario con stock disponible.");
		}
		inventario.inactivar();
		inventarioRepository.save(inventario);
	}

	private void validarStockNoNegativo(Integer stock, String mensaje) {
		if (stock == null || stock < 0) {
			throw new InventarioInvalidoException(mensaje);
		}
	}

	private Inventario buscarPorId(UUID id) {
		return inventarioRepository.findById(id)
				.orElseThrow(() -> new InventarioNoEncontradoException(id));
	}

	private Producto buscarProductoActivo(UUID productoId) {
		Producto producto = productoRepository.findById(productoId)
				.orElseThrow(() -> new ProductoNoEncontradoException(productoId));

		if (producto.getEstado() != EstadoProducto.ACTIVO) {
			throw new ProductoInactivoParaInventarioException(productoId);
		}

		return producto;
	}

	private InventarioResponse toResponse(Inventario inventario) {
		return new InventarioResponse(
				inventario.getId(),
				inventario.getProducto().getId(),
				inventario.getProducto().getNombre(),
				inventario.getProducto().getSku(),
				inventario.getStockActual(),
				inventario.getStockMinimo(),
				inventario.getUbicacion(),
				com.titishop.inventario.dto.EstadoInventario.valueOf(inventario.getEstado().name()),
				inventario.esStockCritico(),
				inventario.getCreadoEn(),
				inventario.getActualizadoEn()
		);
	}
}
