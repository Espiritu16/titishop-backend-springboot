package com.titishop.productos.controller;

import com.titishop.productos.dto.ActualizarMarcaRequest;
import com.titishop.productos.dto.CrearMarcaRequest;
import com.titishop.productos.dto.MarcaResponse;
import com.titishop.productos.service.MarcaService;
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
@RequestMapping("/api/marcas")
public class MarcaController {

	private final MarcaService marcaService;

	public MarcaController(MarcaService marcaService) {
		this.marcaService = marcaService;
	}

	@GetMapping
	public List<MarcaResponse> listar() {
		return marcaService.listar();
	}

	@GetMapping("/{id}")
	public MarcaResponse obtenerPorId(@PathVariable UUID id) {
		return marcaService.obtenerPorId(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MarcaResponse crear(@Valid @RequestBody CrearMarcaRequest request) {
		return marcaService.crear(request);
	}

	@PutMapping("/{id}")
	public MarcaResponse actualizar(@PathVariable UUID id, @Valid @RequestBody ActualizarMarcaRequest request) {
		return marcaService.actualizar(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void inactivar(@PathVariable UUID id) {
		marcaService.inactivar(id);
	}
}
