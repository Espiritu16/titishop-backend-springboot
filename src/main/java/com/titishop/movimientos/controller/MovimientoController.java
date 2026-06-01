package com.titishop.movimientos.controller;

import com.titishop.movimientos.dto.AnularMovimientoRequest;
import com.titishop.movimientos.dto.MovimientoResponse;
import com.titishop.movimientos.dto.RegistrarMovimientoRequest;
import com.titishop.movimientos.service.MovimientoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/movimientos")
public class MovimientoController {

	private final MovimientoService movimientoService;

	public MovimientoController(MovimientoService movimientoService) {
		this.movimientoService = movimientoService;
	}

	@GetMapping
	public List<MovimientoResponse> listar() {
		return movimientoService.listar();
	}

	@GetMapping("/{id}")
	public MovimientoResponse obtenerPorId(@PathVariable UUID id) {
		return movimientoService.obtenerPorId(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MovimientoResponse registrar(@Valid @RequestBody RegistrarMovimientoRequest request) {
		return movimientoService.registrar(request);
	}

	@PostMapping("/{id}/anulacion")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void anular(@PathVariable UUID id, @Valid @RequestBody AnularMovimientoRequest request) {
		movimientoService.anular(id, request.usuarioId(), request.motivoAnulacion());
	}
}
