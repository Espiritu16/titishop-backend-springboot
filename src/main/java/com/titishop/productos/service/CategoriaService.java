package com.titishop.productos.service;

import com.titishop.productos.dto.ActualizarCategoriaRequest;
import com.titishop.productos.dto.CategoriaResponse;
import com.titishop.productos.dto.CrearCategoriaRequest;
import com.titishop.productos.exception.CategoriaNoEncontradaException;
import com.titishop.productos.exception.NombreCategoriaDuplicadoException;
import com.titishop.productos.repository.CategoriaRepository;
import java.util.List;
import java.util.UUID;
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
		return categoriaRepository.findAll().stream()
				.map(this::toResponse)
				.toList();
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
