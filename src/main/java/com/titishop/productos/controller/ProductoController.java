package com.titishop.productos.controller;

import com.titishop.productos.dto.ActualizarProductoRequest;
import com.titishop.productos.dto.CrearProductoRequest;
import com.titishop.productos.dto.ProductoResponse;
import com.titishop.productos.service.ProductoService;
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
@RequestMapping("/api/productos")
public class ProductoController {

	private final ProductoService productoService;

	public ProductoController(ProductoService productoService) {
		this.productoService = productoService;
	}

	@GetMapping
	public List<ProductoResponse> listar() {
		return productoService.listar();
	}

	@GetMapping("/{id}")
	public ProductoResponse obtenerPorId(@PathVariable UUID id) {
		return productoService.obtenerPorId(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProductoResponse crear(@Valid @RequestBody CrearProductoRequest request) {
		return productoService.crear(request);
	}

	@PutMapping("/{id}")
	public ProductoResponse actualizar(@PathVariable UUID id, @Valid @RequestBody ActualizarProductoRequest request) {
		return productoService.actualizar(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void inactivar(@PathVariable UUID id) {
		productoService.inactivar(id);
	}
}
