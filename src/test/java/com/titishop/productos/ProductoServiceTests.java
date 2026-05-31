package com.titishop.productos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.titishop.productos.dto.ActualizarProductoRequest;
import com.titishop.productos.dto.CrearProductoRequest;
import com.titishop.productos.dto.EstadoProducto;
import com.titishop.productos.entity.Categoria;
import com.titishop.productos.entity.Marca;
import com.titishop.productos.entity.Producto;
import com.titishop.productos.exception.ProductoNoEncontradoException;
import com.titishop.productos.exception.SkuDuplicadoException;
import com.titishop.productos.repository.CategoriaRepository;
import com.titishop.productos.repository.MarcaRepository;
import com.titishop.productos.repository.ProductoRepository;
import com.titishop.productos.service.ProductoService;
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
class ProductoServiceTests {

	@Mock
	private ProductoRepository productoRepository;
	@Mock
	private CategoriaRepository categoriaRepository;
	@Mock
	private MarcaRepository marcaRepository;

	private ProductoService productoService;

	@BeforeEach
	void setUp() {
		productoService = new ProductoService(productoRepository, categoriaRepository, marcaRepository);
	}

	@Test
	void crearFallaConSkuDuplicado() {
		CrearProductoRequest request = new CrearProductoRequest(
				"Teclado", "sku-1", "Mecanico", null, UUID.randomUUID(), UUID.randomUUID(), BigDecimal.ONE, BigDecimal.TEN
		);
		when(productoRepository.existsBySkuIgnoreCase("SKU-1")).thenReturn(true);

		assertThatThrownBy(() -> productoService.crear(request))
				.isInstanceOf(SkuDuplicadoException.class);
	}

	@Test
	void actualizarFallaSiNoExisteProducto() {
		UUID id = UUID.randomUUID();
		ActualizarProductoRequest request = new ActualizarProductoRequest(
				"Mouse", "sku-2", "Optico", null, UUID.randomUUID(), UUID.randomUUID(), BigDecimal.ONE, BigDecimal.TEN, EstadoProducto.ACTIVO
		);
		when(productoRepository.findById(id)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> productoService.actualizar(id, request))
				.isInstanceOf(ProductoNoEncontradoException.class);
	}

	@Test
	void inactivarMarcaEstadoInactivo() {
		UUID id = UUID.randomUUID();
		Categoria categoria = new Categoria("Perifericos");
		Marca marca = new Marca();
		ReflectionTestUtils.setField(marca, "id", UUID.randomUUID());
		ReflectionTestUtils.setField(marca, "nombre", "Logi");
		Producto producto = new Producto("Mouse", "SKU-3", "Optico", null, categoria, marca, BigDecimal.ONE, BigDecimal.TEN);

		when(productoRepository.findById(id)).thenReturn(Optional.of(producto));

		productoService.inactivar(id);

		assertThat(producto.getEstado().name()).isEqualTo("INACTIVO");
		verify(productoRepository).save(producto);
	}
}
