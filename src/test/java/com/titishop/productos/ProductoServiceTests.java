package com.titishop.productos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;

import com.titishop.productos.dto.ActualizarProductoRequest;
import com.titishop.productos.dto.CrearProductoRequest;
import com.titishop.productos.dto.EstadoProducto;
import com.titishop.productos.entity.Categoria;
import com.titishop.productos.entity.Marca;
import com.titishop.productos.entity.Producto;
import com.titishop.productos.exception.CategoriaInactivaParaProductoException;
import com.titishop.productos.exception.MarcaInactivaParaProductoException;
import com.titishop.productos.exception.ProductoInvalidoException;
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
	void actualizarFallaSiNoHayCambios() {
		UUID id = UUID.randomUUID();
		UUID categoriaId = UUID.randomUUID();
		UUID marcaId = UUID.randomUUID();
		Categoria categoria = new Categoria("Perifericos");
		Marca marca = new Marca("Logi");
		ReflectionTestUtils.setField(categoria, "id", categoriaId);
		ReflectionTestUtils.setField(marca, "id", marcaId);
		Producto producto = new Producto("Mouse", "SKU-2", "Optico", null, categoria, marca, BigDecimal.ONE, BigDecimal.TEN);
		ActualizarProductoRequest request = new ActualizarProductoRequest(
				" Mouse ", "sku-2", " Optico ", "", categoriaId, marcaId, BigDecimal.ONE, BigDecimal.TEN, EstadoProducto.ACTIVO
		);

		when(productoRepository.findById(id)).thenReturn(Optional.of(producto));
		when(productoRepository.existsBySkuIgnoreCaseAndIdNot("SKU-2", id)).thenReturn(false);
		when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
		when(marcaRepository.findById(marcaId)).thenReturn(Optional.of(marca));

		assertThatThrownBy(() -> productoService.actualizar(id, request))
				.hasMessage("No hay cambios para actualizar.");
		verify(productoRepository, never()).save(any(Producto.class));
	}

	@Test
	void crearFallaSiCategoriaEstaInactiva() {
		UUID categoriaId = UUID.randomUUID();
		UUID marcaId = UUID.randomUUID();
		CrearProductoRequest request = new CrearProductoRequest(
				"Mouse", "sku-cat", "Optico", null, categoriaId, marcaId, BigDecimal.ONE, BigDecimal.TEN
		);
		Categoria categoria = new Categoria("Perifericos");
		ReflectionTestUtils.setField(categoria, "estado", com.titishop.productos.entity.EstadoCatalogo.INACTIVO);

		when(productoRepository.existsBySkuIgnoreCase("SKU-CAT")).thenReturn(false);
		when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));

		assertThatThrownBy(() -> productoService.crear(request))
				.isInstanceOf(CategoriaInactivaParaProductoException.class);
	}

	@Test
	void crearFallaSiMarcaEstaInactiva() {
		UUID categoriaId = UUID.randomUUID();
		UUID marcaId = UUID.randomUUID();
		CrearProductoRequest request = new CrearProductoRequest(
				"Mouse", "sku-marca", "Optico", null, categoriaId, marcaId, BigDecimal.ONE, BigDecimal.TEN
		);
		Categoria categoria = new Categoria("Perifericos");
		Marca marca = new Marca("Logi");
		ReflectionTestUtils.setField(marca, "estado", com.titishop.productos.entity.EstadoCatalogo.INACTIVO);

		when(productoRepository.existsBySkuIgnoreCase("SKU-MARCA")).thenReturn(false);
		when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
		when(marcaRepository.findById(marcaId)).thenReturn(Optional.of(marca));

		assertThatThrownBy(() -> productoService.crear(request))
				.isInstanceOf(MarcaInactivaParaProductoException.class);
	}

	@Test
	void crearFallaSiPrecioVentaEsMenorQueCompra() {
		CrearProductoRequest request = new CrearProductoRequest(
				"Mouse", "sku-precio", "Optico", null, UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, BigDecimal.ONE
		);

		assertThatThrownBy(() -> productoService.crear(request))
				.isInstanceOf(ProductoInvalidoException.class);
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
