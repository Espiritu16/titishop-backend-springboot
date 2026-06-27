package com.titishop.productos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import com.titishop.productos.dto.ActualizarCategoriaRequest;
import com.titishop.productos.dto.CrearCategoriaRequest;
import com.titishop.productos.dto.EstadoCatalogo;
import com.titishop.productos.entity.Categoria;
import com.titishop.productos.exception.CategoriaNoEncontradaException;
import com.titishop.productos.exception.NombreCategoriaDuplicadoException;
import com.titishop.productos.repository.CategoriaRepository;
import com.titishop.productos.service.CategoriaService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTests {

	@Mock
	private CategoriaRepository categoriaRepository;

	private CategoriaService categoriaService;

	@BeforeEach
	void setUp() {
		categoriaService = new CategoriaService(categoriaRepository);
	}

	@Test
	void crearFallaSiNombreDuplicado() {
		when(categoriaRepository.existsByNombreIgnoreCase("Perifericos")).thenReturn(true);
		assertThatThrownBy(() -> categoriaService.crear(new CrearCategoriaRequest("Perifericos")))
				.isInstanceOf(NombreCategoriaDuplicadoException.class);
	}

	@Test
	void actualizarFallaSiNoExiste() {
		UUID id = UUID.randomUUID();
		when(categoriaRepository.findById(id)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> categoriaService.actualizar(id, new ActualizarCategoriaRequest("Audio", EstadoCatalogo.ACTIVO)))
				.isInstanceOf(CategoriaNoEncontradaException.class);
	}

	@Test
	void actualizarFallaSiNoHayCambios() {
		UUID id = UUID.randomUUID();
		Categoria categoria = new Categoria("Audio");
		when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));

		assertThatThrownBy(() -> categoriaService.actualizar(id, new ActualizarCategoriaRequest(" Audio ", EstadoCatalogo.ACTIVO)))
				.hasMessage("No hay cambios para actualizar.");
		verify(categoriaRepository, never()).save(categoria);
	}

	@Test
	void inactivarMarcaInactivo() {
		UUID id = UUID.randomUUID();
		Categoria categoria = new Categoria("Audio");
		when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));

		categoriaService.inactivar(id);

		assertThat(categoria.getEstado().name()).isEqualTo("INACTIVO");
		verify(categoriaRepository).save(categoria);
	}
}
