package com.titishop.proveedores.controller;

import com.titishop.proveedores.dto.ActualizarProveedorRequest;
import com.titishop.proveedores.dto.ConsultaRucProveedorResponse;
import com.titishop.proveedores.dto.CrearProveedorRequest;
import com.titishop.proveedores.dto.ProveedorResponse;
import com.titishop.proveedores.service.ProveedorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
@Validated
@RequestMapping("/api/proveedores")
public class ProveedorController {

	private final ProveedorService proveedorService;

	public ProveedorController(ProveedorService proveedorService) {
		this.proveedorService = proveedorService;
	}

	@GetMapping
	public List<ProveedorResponse> listar() {
		return proveedorService.listar();
	}

	@GetMapping("/{id}")
	public ProveedorResponse obtenerPorId(@PathVariable UUID id) {
		return proveedorService.obtenerPorId(id);
	}

	@GetMapping("/consulta-ruc/{ruc}")
	public ConsultaRucProveedorResponse consultarRuc(
			@PathVariable @Pattern(regexp = "\\d{11}", message = "ruc debe tener 11 digitos") String ruc
	) {
		return proveedorService.consultarRuc(ruc);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProveedorResponse crear(@Valid @RequestBody CrearProveedorRequest request) {
		return proveedorService.crear(request);
	}

	@PutMapping("/{id}")
	public ProveedorResponse actualizar(@PathVariable UUID id, @Valid @RequestBody ActualizarProveedorRequest request) {
		return proveedorService.actualizar(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void inactivar(@PathVariable UUID id) {
		proveedorService.inactivar(id);
	}
}
