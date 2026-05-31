package com.titishop.inventario.controller;

import com.titishop.inventario.dto.ActualizarInventarioRequest;
import com.titishop.inventario.dto.CrearInventarioRequest;
import com.titishop.inventario.dto.InventarioResponse;
import com.titishop.inventario.service.InventarioService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

	private final InventarioService inventarioService;

	public InventarioController(InventarioService inventarioService) {
		this.inventarioService = inventarioService;
	}

	@GetMapping
	public List<InventarioResponse> listar() {
		return inventarioService.listar();
	}

	@GetMapping("/{id}")
	public InventarioResponse obtenerPorId(@PathVariable UUID id) {
		return inventarioService.obtenerPorId(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public InventarioResponse crear(@Valid @RequestBody CrearInventarioRequest request) {
		return inventarioService.crear(request);
	}

	@PutMapping("/{id}")
	public InventarioResponse actualizar(@PathVariable UUID id, @Valid @RequestBody ActualizarInventarioRequest request) {
		return inventarioService.actualizar(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void inactivar(@PathVariable UUID id) {
		inventarioService.inactivar(id);
	}
}
