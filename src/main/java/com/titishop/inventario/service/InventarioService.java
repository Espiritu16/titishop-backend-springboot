package com.titishop.inventario.service;

import com.titishop.inventario.repository.InventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventarioService {

	private final InventarioRepository inventarioRepository;

	public InventarioService(InventarioRepository inventarioRepository) {
		this.inventarioRepository = inventarioRepository;
	}
}
