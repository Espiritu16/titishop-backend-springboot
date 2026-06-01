package com.titishop.productos.service;

import com.titishop.productos.dto.ActualizarProductoRequest;
import com.titishop.productos.dto.CrearProductoRequest;
import com.titishop.productos.dto.ProductoResponse;
import com.titishop.productos.entity.Categoria;
import com.titishop.productos.entity.EstadoCatalogo;
import com.titishop.productos.entity.Marca;
import com.titishop.productos.entity.Producto;
import com.titishop.productos.exception.CategoriaInactivaParaProductoException;
import com.titishop.productos.exception.CategoriaNoEncontradaException;
import com.titishop.productos.exception.MarcaInactivaParaProductoException;
import com.titishop.productos.exception.MarcaNoEncontradaException;
import com.titishop.productos.exception.ProductoInvalidoException;
import com.titishop.productos.exception.ProductoNoEncontradoException;
import com.titishop.productos.exception.SkuDuplicadoException;
import com.titishop.productos.repository.CategoriaRepository;
import com.titishop.productos.repository.MarcaRepository;
import com.titishop.productos.repository.ProductoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductoService {

	private final ProductoRepository productoRepository;
	private final CategoriaRepository categoriaRepository;
	private final MarcaRepository marcaRepository;

	public ProductoService(
			ProductoRepository productoRepository,
			CategoriaRepository categoriaRepository,
			MarcaRepository marcaRepository
	) {
		this.productoRepository = productoRepository;
		this.categoriaRepository = categoriaRepository;
		this.marcaRepository = marcaRepository;
	}

	@Transactional(readOnly = true)
	public List<ProductoResponse> listar() {
		return productoRepository.findAll().stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public ProductoResponse obtenerPorId(UUID id) {
		return toResponse(buscarPorId(id));
	}

	public ProductoResponse crear(CrearProductoRequest request) {
		validarPrecios(request.precioCompra(), request.precioVenta());
		String sku = normalizarSku(request.sku());
		if (productoRepository.existsBySkuIgnoreCase(sku)) {
			throw new SkuDuplicadoException(sku);
		}

		Categoria categoria = buscarCategoria(request.categoriaId());
		Marca marca = buscarMarca(request.marcaId());

		Producto producto = new Producto(
				request.nombre().trim(),
				sku,
				request.descripcion().trim(),
				normalizarImagen(request.imagenUrl()),
				categoria,
				marca,
				request.precioCompra(),
				request.precioVenta()
		);
		return toResponse(productoRepository.save(producto));
	}

	public ProductoResponse actualizar(UUID id, ActualizarProductoRequest request) {
		Producto producto = buscarPorId(id);
		validarPrecios(request.precioCompra(), request.precioVenta());
		String sku = normalizarSku(request.sku());
		if (productoRepository.existsBySkuIgnoreCaseAndIdNot(sku, id)) {
			throw new SkuDuplicadoException(sku);
		}

		Categoria categoria = buscarCategoria(request.categoriaId());
		Marca marca = buscarMarca(request.marcaId());

		producto.actualizar(
				request.nombre().trim(),
				sku,
				request.descripcion().trim(),
				normalizarImagen(request.imagenUrl()),
				categoria,
				marca,
				request.precioCompra(),
				request.precioVenta(),
				com.titishop.productos.entity.EstadoProducto.valueOf(request.estado().name())
		);
		return toResponse(productoRepository.save(producto));
	}

	public void inactivar(UUID id) {
		Producto producto = buscarPorId(id);
		producto.inactivar();
		productoRepository.save(producto);
	}

	private Producto buscarPorId(UUID id) {
		return productoRepository.findById(id)
				.orElseThrow(() -> new ProductoNoEncontradoException(id));
	}

	private Categoria buscarCategoria(UUID categoriaId) {
		Categoria categoria = categoriaRepository.findById(categoriaId)
				.orElseThrow(() -> new CategoriaNoEncontradaException(categoriaId));
		if (categoria.getEstado() != EstadoCatalogo.ACTIVO) {
			throw new CategoriaInactivaParaProductoException(categoriaId);
		}
		return categoria;
	}

	private Marca buscarMarca(UUID marcaId) {
		Marca marca = marcaRepository.findById(marcaId)
				.orElseThrow(() -> new MarcaNoEncontradaException(marcaId));
		if (marca.getEstado() != EstadoCatalogo.ACTIVO) {
			throw new MarcaInactivaParaProductoException(marcaId);
		}
		return marca;
	}

	private void validarPrecios(BigDecimal precioCompra, BigDecimal precioVenta) {
		if (precioCompra.signum() < 0 || precioVenta.signum() < 0) {
			throw new ProductoInvalidoException("Los precios no pueden ser negativos.");
		}
		if (precioVenta.compareTo(precioCompra) < 0) {
			throw new ProductoInvalidoException("El precio de venta no puede ser menor al precio de compra.");
		}
	}

	private String normalizarSku(String sku) {
		return sku.trim().toUpperCase(Locale.ROOT);
	}

	private String normalizarImagen(String imagenUrl) {
		if (imagenUrl == null) {
			return null;
		}
		String value = imagenUrl.trim();
		return value.isEmpty() ? null : value;
	}

	private ProductoResponse toResponse(Producto producto) {
		return new ProductoResponse(
				producto.getId(),
				producto.getNombre(),
				producto.getSku(),
				producto.getDescripcion(),
				producto.getImagenUrl(),
				producto.getCategoria().getId(),
				producto.getCategoria().getNombre(),
				producto.getMarca().getId(),
				producto.getMarca().getNombre(),
				producto.getPrecioCompra(),
				producto.getPrecioVenta(),
				com.titishop.productos.dto.EstadoProducto.valueOf(producto.getEstado().name()),
				producto.getCreadoEn(),
				producto.getActualizadoEn()
		);
	}
}
