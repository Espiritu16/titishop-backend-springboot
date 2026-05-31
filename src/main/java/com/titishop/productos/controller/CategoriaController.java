package com.titishop.productos.controller;

import com.titishop.productos.dto.ActualizarCategoriaRequest;
import com.titishop.productos.dto.CategoriaResponse;
import com.titishop.productos.dto.CrearCategoriaRequest;
import com.titishop.productos.service.CategoriaService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

	private final CategoriaService categoriaService;

	public CategoriaController(CategoriaService categoriaService) {
		this.categoriaService = categoriaService;
	}

	@GetMapping
	public List<CategoriaResponse> listar() {
		return categoriaService.listar();
	}

	@GetMapping("/{id}")
	public CategoriaResponse obtenerPorId(@PathVariable UUID id) {
		return categoriaService.obtenerPorId(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CategoriaResponse crear(@Valid @RequestBody CrearCategoriaRequest request) {
		return categoriaService.crear(request);
	}

	@PutMapping("/{id}")
	public CategoriaResponse actualizar(@PathVariable UUID id, @Valid @RequestBody ActualizarCategoriaRequest request) {
		return categoriaService.actualizar(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void inactivar(@PathVariable UUID id) {
		categoriaService.inactivar(id);
	}
}
