package com.titishop.inventario.service;

import com.titishop.compartido.response.PaginaResponse;
import com.titishop.inventario.dto.ActualizarInventarioRequest;
import com.titishop.inventario.dto.CrearInventarioRequest;
import com.titishop.inventario.dto.EstadoInventario;
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
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
		return listar(0, Integer.MAX_VALUE, null, null, null).content();
	}

	@Transactional(readOnly = true)
	public PaginaResponse<InventarioResponse> listar(int page, int size, String busqueda, EstadoInventario estado, String stockEstado) {
		var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "creadoEn"));
		var pagina = inventarioRepository.findAll(construirFiltro(busqueda, estado, normalizarStockEstado(stockEstado)), pageable)
				.map(this::toResponse);
		return new PaginaResponse<>(
				pagina.getContent(),
				pagina.getNumber(),
				pagina.getSize(),
				pagina.getTotalElements(),
				pagina.getTotalPages(),
				pagina.isFirst(),
				pagina.isLast(),
				pagina.isEmpty()
		);
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

	private Specification<Inventario> construirFiltro(String busqueda, EstadoInventario estado, String stockEstadoNormalizado) {
		return (root, query, cb) -> {
			var predicates = cb.conjunction();
			String texto = busqueda == null ? "" : busqueda.trim().toLowerCase(Locale.ROOT);
			if (!texto.isEmpty()) {
				String like = "%" + texto + "%";
				var producto = root.join("producto");
				predicates = cb.and(predicates, cb.or(
						cb.like(cb.lower(producto.get("nombre")), like),
						cb.like(cb.lower(producto.get("sku")), like),
						cb.like(cb.lower(root.get("ubicacion")), like)
				));
			}
			if (estado != null) {
				predicates = cb.and(
						predicates,
						cb.equal(root.get("estado"), com.titishop.inventario.entity.EstadoInventario.valueOf(estado.name()))
				);
			}
			if (!stockEstadoNormalizado.isEmpty()) {
				switch (stockEstadoNormalizado) {
					case "NORMAL" ->
						predicates = cb.and(predicates, cb.greaterThan(root.get("stockActual"), root.get("stockMinimo")));
					case "BAJO" ->
						predicates = cb.and(
								predicates,
								cb.greaterThan(root.get("stockActual"), 0),
								cb.lessThanOrEqualTo(root.get("stockActual"), root.get("stockMinimo"))
						);
					case "AGOTADO" ->
						predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("stockActual"), 0));
					default -> {
						throw new IllegalArgumentException("stockEstado debe ser NORMAL, BAJO o AGOTADO.");
					}
				}
			}
			return predicates;
		};
	}

	private String normalizarStockEstado(String stockEstado) {
		String stockEstadoNormalizado = stockEstado == null ? "" : stockEstado.trim().toUpperCase(Locale.ROOT);
		if (!stockEstadoNormalizado.isEmpty()
				&& !stockEstadoNormalizado.equals("NORMAL")
				&& !stockEstadoNormalizado.equals("BAJO")
				&& !stockEstadoNormalizado.equals("AGOTADO")) {
			throw new IllegalArgumentException("stockEstado debe ser NORMAL, BAJO o AGOTADO.");
		}
		return stockEstadoNormalizado;
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
