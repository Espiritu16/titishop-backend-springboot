package com.titishop.movimientos.service;

import com.titishop.movimientos.repository.MovimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MovimientoService {

	private final MovimientoRepository movimientoRepository;

	public MovimientoService(MovimientoRepository movimientoRepository) {
		this.movimientoRepository = movimientoRepository;
	}
}
