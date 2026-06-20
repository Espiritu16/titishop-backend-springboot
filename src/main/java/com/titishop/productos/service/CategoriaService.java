package com.titishop.productos.service;

import com.titishop.compartido.response.PaginaResponse;
import com.titishop.productos.dto.ActualizarCategoriaRequest;
import com.titishop.productos.dto.CategoriaResponse;
import com.titishop.productos.dto.CrearCategoriaRequest;
import com.titishop.productos.dto.EstadoCatalogo;
import com.titishop.productos.exception.CategoriaNoEncontradaException;
import com.titishop.productos.exception.NombreCategoriaDuplicadoException;
import com.titishop.productos.repository.CategoriaRepository;
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
public class CategoriaService {

	private final CategoriaRepository categoriaRepository;

	public CategoriaService(CategoriaRepository categoriaRepository) {
		this.categoriaRepository = categoriaRepository;
	}

	@Transactional(readOnly = true)
	public List<CategoriaResponse> listar() {
		return listar(0, Integer.MAX_VALUE, null, null).content();
	}

	@Transactional(readOnly = true)
	public PaginaResponse<CategoriaResponse> listar(int page, int size, String busqueda, EstadoCatalogo estado) {
		var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "creadoEn"));
		var pagina = categoriaRepository.findAll(construirFiltro(busqueda, estado), pageable).map(this::toResponse);
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
	public CategoriaResponse obtenerPorId(UUID id) {
		return toResponse(buscarPorId(id));
	}

	public CategoriaResponse crear(CrearCategoriaRequest request) {
		String nombre = normalizarNombre(request.nombre());
		if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
			throw new NombreCategoriaDuplicadoException(nombre);
		}
		com.titishop.productos.entity.Categoria categoria = new com.titishop.productos.entity.Categoria(nombre);
		return toResponse(categoriaRepository.save(categoria));
	}

	public CategoriaResponse actualizar(UUID id, ActualizarCategoriaRequest request) {
		com.titishop.productos.entity.Categoria categoria = buscarPorId(id);
		String nombre = normalizarNombre(request.nombre());
		if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
			throw new NombreCategoriaDuplicadoException(nombre);
		}

		categoria.actualizar(nombre, com.titishop.productos.entity.EstadoCatalogo.valueOf(request.estado().name()));
		return toResponse(categoriaRepository.save(categoria));
	}

	public void inactivar(UUID id) {
		com.titishop.productos.entity.Categoria categoria = buscarPorId(id);
		categoria.inactivar();
		categoriaRepository.save(categoria);
	}

	private com.titishop.productos.entity.Categoria buscarPorId(UUID id) {
		return categoriaRepository.findById(id)
				.orElseThrow(() -> new CategoriaNoEncontradaException(id));
	}

	private String normalizarNombre(String nombre) {
		return nombre.trim();
	}

	private Specification<com.titishop.productos.entity.Categoria> construirFiltro(String busqueda, EstadoCatalogo estado) {
		return (root, query, cb) -> {
			var predicates = cb.conjunction();
			String texto = busqueda == null ? "" : busqueda.trim().toLowerCase(Locale.ROOT);
			if (!texto.isEmpty()) {
				predicates = cb.and(predicates, cb.like(cb.lower(root.get("nombre")), "%" + texto + "%"));
			}
			if (estado != null) {
				predicates = cb.and(
						predicates,
						cb.equal(root.get("estado"), com.titishop.productos.entity.EstadoCatalogo.valueOf(estado.name()))
				);
			}
			return predicates;
		};
	}

	private CategoriaResponse toResponse(com.titishop.productos.entity.Categoria categoria) {
		return new CategoriaResponse(
				categoria.getId(),
				categoria.getNombre(),
				com.titishop.productos.dto.EstadoCatalogo.valueOf(categoria.getEstado().name()),
				categoria.getCreadoEn(),
				categoria.getActualizadoEn()
		);
	}
}
