package com.titishop.inventario;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.titishop.inventario.dto.ActualizarInventarioRequest;
import com.titishop.inventario.dto.CrearInventarioRequest;
import com.titishop.inventario.dto.EstadoInventario;
import com.titishop.inventario.entity.Inventario;
import com.titishop.inventario.exception.InventarioDuplicadoPorProductoException;
import com.titishop.inventario.exception.InventarioInvalidoException;
import com.titishop.inventario.exception.ProductoInactivoParaInventarioException;
import com.titishop.inventario.repository.InventarioRepository;
import com.titishop.inventario.service.InventarioService;
import com.titishop.productos.entity.Categoria;
import com.titishop.productos.entity.Marca;
import com.titishop.productos.entity.Producto;
import com.titishop.productos.repository.ProductoRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTests {

	@Mock
	private InventarioRepository inventarioRepository;
	@Mock
	private ProductoRepository productoRepository;

	private InventarioService inventarioService;

	@BeforeEach
	void setUp() {
		inventarioService = new InventarioService(inventarioRepository, productoRepository);
	}

	@Test
	void crearFallaSiProductoYaTieneInventario() {
		UUID productoId = UUID.randomUUID();
		CrearInventarioRequest request = new CrearInventarioRequest(productoId, 10, 5, "A-01");
		Producto producto = productoActivo(productoId);

		when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
		when(inventarioRepository.existsByProductoId(productoId)).thenReturn(true);

		assertThatThrownBy(() -> inventarioService.crear(request))
				.isInstanceOf(InventarioDuplicadoPorProductoException.class);
	}

	@Test
	void crearFallaSiProductoInactivo() {
		UUID productoId = UUID.randomUUID();
		CrearInventarioRequest request = new CrearInventarioRequest(productoId, 10, 5, "A-01");
		Producto producto = productoActivo(productoId);
		ReflectionTestUtils.setField(producto, "estado", com.titishop.productos.entity.EstadoProducto.INACTIVO);

		when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

		assertThatThrownBy(() -> inventarioService.crear(request))
				.isInstanceOf(ProductoInactivoParaInventarioException.class);
	}

	@Test
	void inactivarActualizaEstado() {
		UUID inventarioId = UUID.randomUUID();
		Inventario inventario = new Inventario(productoActivo(UUID.randomUUID()), 0, 5, "A-01");
		when(inventarioRepository.findById(inventarioId)).thenReturn(Optional.of(inventario));

		inventarioService.inactivar(inventarioId);

		verify(inventarioRepository).save(inventario);
	}

	@Test
	void crearFallaSiStockActualEsNegativo() {
		CrearInventarioRequest request = new CrearInventarioRequest(UUID.randomUUID(), -1, 5, "A-01");

		assertThatThrownBy(() -> inventarioService.crear(request))
				.isInstanceOf(InventarioInvalidoException.class);
		verify(inventarioRepository, never()).save(any(Inventario.class));
	}

	@Test
	void actualizarFallaSiStockMinimoEsNegativo() {
		UUID inventarioId = UUID.randomUUID();
		ActualizarInventarioRequest request = new ActualizarInventarioRequest(-1, "A-01", EstadoInventario.ACTIVO);

		assertThatThrownBy(() -> inventarioService.actualizar(inventarioId, request))
				.isInstanceOf(InventarioInvalidoException.class);
		verify(inventarioRepository, never()).save(any(Inventario.class));
	}

	@Test
	void inactivarFallaSiMantieneStockDisponible() {
		UUID inventarioId = UUID.randomUUID();
		Inventario inventario = new Inventario(productoActivo(UUID.randomUUID()), 10, 5, "A-01");
		when(inventarioRepository.findById(inventarioId)).thenReturn(Optional.of(inventario));

		assertThatThrownBy(() -> inventarioService.inactivar(inventarioId))
				.isInstanceOf(InventarioInvalidoException.class);
		verify(inventarioRepository, never()).save(inventario);
	}

	private Producto productoActivo(UUID id) {
		Categoria categoria = new Categoria("Perifericos");
		Marca marca = new Marca();
		ReflectionTestUtils.setField(marca, "id", UUID.randomUUID());
		ReflectionTestUtils.setField(marca, "nombre", "Logi");
		Producto producto = new Producto("Mouse", "SKU-INV", "Optico", null, categoria, marca, BigDecimal.ONE, BigDecimal.TEN);
		ReflectionTestUtils.setField(producto, "id", id);
		return producto;
	}
}
