package com.titishop.productos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.titishop.productos.dto.ActualizarMarcaRequest;
import com.titishop.productos.dto.CrearMarcaRequest;
import com.titishop.productos.dto.EstadoCatalogo;
import com.titishop.productos.entity.Marca;
import com.titishop.productos.exception.MarcaNoEncontradaException;
import com.titishop.productos.exception.NombreMarcaDuplicadoException;
import com.titishop.productos.repository.MarcaRepository;
import com.titishop.productos.service.MarcaService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MarcaServiceTests {

	@Mock
	private MarcaRepository marcaRepository;

	private MarcaService marcaService;

	@BeforeEach
	void setUp() {
		marcaService = new MarcaService(marcaRepository);
	}

	@Test
	void crearFallaSiNombreDuplicado() {
		when(marcaRepository.existsByNombreIgnoreCase("Logitech")).thenReturn(true);
		assertThatThrownBy(() -> marcaService.crear(new CrearMarcaRequest("Logitech")))
				.isInstanceOf(NombreMarcaDuplicadoException.class);
	}

	@Test
	void actualizarFallaSiNoExiste() {
		UUID id = UUID.randomUUID();
		when(marcaRepository.findById(id)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> marcaService.actualizar(id, new ActualizarMarcaRequest("Logitech", EstadoCatalogo.ACTIVO)))
				.isInstanceOf(MarcaNoEncontradaException.class);
	}

	@Test
	void inactivarMarcaEstadoInactivo() {
		UUID id = UUID.randomUUID();
		Marca marca = new Marca("Logitech");
		when(marcaRepository.findById(id)).thenReturn(Optional.of(marca));

		marcaService.inactivar(id);

		assertThat(marca.getEstado().name()).isEqualTo("INACTIVO");
		verify(marcaRepository).save(marca);
	}
}
