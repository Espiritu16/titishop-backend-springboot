package com.titishop.productos.service;

import com.titishop.compartido.response.PaginaResponse;
import com.titishop.productos.dto.ActualizarMarcaRequest;
import com.titishop.productos.dto.CrearMarcaRequest;
import com.titishop.productos.dto.EstadoCatalogo;
import com.titishop.productos.dto.MarcaResponse;
import com.titishop.productos.entity.Marca;
import com.titishop.productos.exception.MarcaNoEncontradaException;
import com.titishop.productos.exception.NombreMarcaDuplicadoException;
import com.titishop.productos.repository.MarcaRepository;
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
public class MarcaService {

	private final MarcaRepository marcaRepository;

	public MarcaService(MarcaRepository marcaRepository) {
		this.marcaRepository = marcaRepository;
	}

	@Transactional(readOnly = true)
	public List<MarcaResponse> listar() {
		return listar(0, Integer.MAX_VALUE, null, null).content();
	}

	@Transactional(readOnly = true)
	public PaginaResponse<MarcaResponse> listar(int page, int size, String busqueda, EstadoCatalogo estado) {
		var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "creadoEn"));
		var pagina = marcaRepository.findAll(construirFiltro(busqueda, estado), pageable).map(this::toResponse);
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
	public MarcaResponse obtenerPorId(UUID id) {
		return toResponse(buscarPorId(id));
	}

	public MarcaResponse crear(CrearMarcaRequest request) {
		String nombre = normalizarNombre(request.nombre());
		if (marcaRepository.existsByNombreIgnoreCase(nombre)) {
			throw new NombreMarcaDuplicadoException(nombre);
		}
		Marca marca = new Marca(nombre);
		return toResponse(marcaRepository.save(marca));
	}

	public MarcaResponse actualizar(UUID id, ActualizarMarcaRequest request) {
		Marca marca = buscarPorId(id);
		String nombre = normalizarNombre(request.nombre());
		if (marcaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
			throw new NombreMarcaDuplicadoException(nombre);
		}

		marca.actualizar(nombre, com.titishop.productos.entity.EstadoCatalogo.valueOf(request.estado().name()));
		return toResponse(marcaRepository.save(marca));
	}

	public void inactivar(UUID id) {
		Marca marca = buscarPorId(id);
		marca.inactivar();
		marcaRepository.save(marca);
	}

	private Marca buscarPorId(UUID id) {
		return marcaRepository.findById(id)
				.orElseThrow(() -> new MarcaNoEncontradaException(id));
	}

	private String normalizarNombre(String nombre) {
		return nombre.trim();
	}

	private Specification<Marca> construirFiltro(String busqueda, EstadoCatalogo estado) {
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

	private MarcaResponse toResponse(Marca marca) {
		return new MarcaResponse(
				marca.getId(),
				marca.getNombre(),
				com.titishop.productos.dto.EstadoCatalogo.valueOf(marca.getEstado().name()),
				marca.getCreadoEn(),
				marca.getActualizadoEn()
		);
	}
}
