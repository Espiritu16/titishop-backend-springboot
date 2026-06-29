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
import com.titishop.proveedores.entity.EstadoProveedor;
import com.titishop.proveedores.entity.Proveedor;
import com.titishop.proveedores.exception.ProveedorNoEncontradoException;
import com.titishop.proveedores.repository.ProveedorRepository;
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
	@Mock
	private ProveedorRepository proveedorRepository;

	private ProductoService productoService;

	@BeforeEach
	void setUp() {
		productoService = new ProductoService(productoRepository, categoriaRepository, marcaRepository, proveedorRepository);
	}

	@Test
	void crearFallaConSkuDuplicado() {
		CrearProductoRequest request = new CrearProductoRequest(
				"Teclado", "sku-1", "Mecanico", null, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "China", BigDecimal.ONE, BigDecimal.TEN
		);
		when(productoRepository.existsBySkuIgnoreCase("SKU-1")).thenReturn(true);

		assertThatThrownBy(() -> productoService.crear(request))
				.isInstanceOf(SkuDuplicadoException.class);
	}

	@Test
	void actualizarFallaSiNoExisteProducto() {
		UUID id = UUID.randomUUID();
		ActualizarProductoRequest request = new ActualizarProductoRequest(
				"Mouse", "sku-2", "Optico", null, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "China", BigDecimal.ONE, BigDecimal.TEN, EstadoProducto.ACTIVO
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
		UUID proveedorId = UUID.randomUUID();
		Categoria categoria = new Categoria("Perifericos");
		Marca marca = new Marca("Logi");
		Proveedor proveedor = proveedorActivo(proveedorId);
		ReflectionTestUtils.setField(categoria, "id", categoriaId);
		ReflectionTestUtils.setField(marca, "id", marcaId);
		Producto producto = new Producto("Mouse", "SKU-2", "Optico", null, categoria, marca, proveedor, "China", BigDecimal.ONE, BigDecimal.TEN);
		ActualizarProductoRequest request = new ActualizarProductoRequest(
				" Mouse ", "sku-2", " Optico ", "", categoriaId, marcaId, proveedorId, " China ", BigDecimal.ONE, BigDecimal.TEN, EstadoProducto.ACTIVO
		);

		when(productoRepository.findById(id)).thenReturn(Optional.of(producto));
		when(productoRepository.existsBySkuIgnoreCaseAndIdNot("SKU-2", id)).thenReturn(false);
		when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
		when(marcaRepository.findById(marcaId)).thenReturn(Optional.of(marca));
		when(proveedorRepository.findById(proveedorId)).thenReturn(Optional.of(proveedor));

		assertThatThrownBy(() -> productoService.actualizar(id, request))
				.hasMessage("No hay cambios para actualizar.");
		verify(productoRepository, never()).save(any(Producto.class));
	}

	@Test
	void crearFallaSiCategoriaEstaInactiva() {
		UUID categoriaId = UUID.randomUUID();
		UUID marcaId = UUID.randomUUID();
		CrearProductoRequest request = new CrearProductoRequest(
				"Mouse", "sku-cat", "Optico", null, categoriaId, marcaId, UUID.randomUUID(), "China", BigDecimal.ONE, BigDecimal.TEN
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
				"Mouse", "sku-marca", "Optico", null, categoriaId, marcaId, UUID.randomUUID(), "China", BigDecimal.ONE, BigDecimal.TEN
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
				"Mouse", "sku-precio", "Optico", null, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "China", BigDecimal.TEN, BigDecimal.ONE
		);

		assertThatThrownBy(() -> productoService.crear(request))
				.isInstanceOf(ProductoInvalidoException.class);
	}

	@Test
	void inactivarMarcaEstadoInactivo() {
		UUID id = UUID.randomUUID();
		Categoria categoria = new Categoria("Perifericos");
		Marca marca = new Marca();
		Proveedor proveedor = proveedorActivo(UUID.randomUUID());
		ReflectionTestUtils.setField(marca, "id", UUID.randomUUID());
		ReflectionTestUtils.setField(marca, "nombre", "Logi");
		Producto producto = new Producto("Mouse", "SKU-3", "Optico", null, categoria, marca, proveedor, "China", BigDecimal.ONE, BigDecimal.TEN);

		when(productoRepository.findById(id)).thenReturn(Optional.of(producto));

		productoService.inactivar(id);

		assertThat(producto.getEstado().name()).isEqualTo("INACTIVO");
		verify(productoRepository).save(producto);
	}

	@Test
	void crearIncluyePaisOrigenYProveedorDirecto() {
		UUID categoriaId = UUID.randomUUID();
		UUID marcaId = UUID.randomUUID();
		UUID proveedorId = UUID.randomUUID();
		Categoria categoria = new Categoria("Perifericos");
		Marca marca = new Marca("Logi");
		Proveedor proveedor = proveedorActivo(proveedorId);
		ReflectionTestUtils.setField(categoria, "id", categoriaId);
		ReflectionTestUtils.setField(marca, "id", marcaId);
		CrearProductoRequest request = new CrearProductoRequest(
				"Mouse", "sku-prov", "Optico", null, categoriaId, marcaId, proveedorId, "  China  ", BigDecimal.ONE, BigDecimal.TEN
		);

		when(productoRepository.existsBySkuIgnoreCase("SKU-PROV")).thenReturn(false);
		when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
		when(marcaRepository.findById(marcaId)).thenReturn(Optional.of(marca));
		when(proveedorRepository.findById(proveedorId)).thenReturn(Optional.of(proveedor));
		when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

		var response = productoService.crear(request);

		assertThat(response.proveedorId()).isEqualTo(proveedorId);
		assertThat(response.proveedorRazonSocial()).isEqualTo("Proveedor Uno");
		assertThat(response.paisOrigen()).isEqualTo("China");
	}

	@Test
	void crearFallaSiProveedorNoExiste() {
		UUID categoriaId = UUID.randomUUID();
		UUID marcaId = UUID.randomUUID();
		UUID proveedorId = UUID.randomUUID();
		Categoria categoria = new Categoria("Perifericos");
		Marca marca = new Marca("Logi");
		CrearProductoRequest request = new CrearProductoRequest(
				"Mouse", "sku-sin-prov", "Optico", null, categoriaId, marcaId, proveedorId, "China", BigDecimal.ONE, BigDecimal.TEN
		);

		when(productoRepository.existsBySkuIgnoreCase("SKU-SIN-PROV")).thenReturn(false);
		when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
		when(marcaRepository.findById(marcaId)).thenReturn(Optional.of(marca));
		when(proveedorRepository.findById(proveedorId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> productoService.crear(request))
				.isInstanceOf(ProveedorNoEncontradoException.class);
	}

	@Test
	void crearFallaSiProveedorEstaInactivo() {
		UUID categoriaId = UUID.randomUUID();
		UUID marcaId = UUID.randomUUID();
		UUID proveedorId = UUID.randomUUID();
		Categoria categoria = new Categoria("Perifericos");
		Marca marca = new Marca("Logi");
		Proveedor proveedor = proveedorActivo(proveedorId);
		ReflectionTestUtils.setField(proveedor, "estado", EstadoProveedor.INACTIVO);
		CrearProductoRequest request = new CrearProductoRequest(
				"Mouse", "sku-prov-inactivo", "Optico", null, categoriaId, marcaId, proveedorId, "China", BigDecimal.ONE, BigDecimal.TEN
		);

		when(productoRepository.existsBySkuIgnoreCase("SKU-PROV-INACTIVO")).thenReturn(false);
		when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
		when(marcaRepository.findById(marcaId)).thenReturn(Optional.of(marca));
		when(proveedorRepository.findById(proveedorId)).thenReturn(Optional.of(proveedor));

		assertThatThrownBy(() -> productoService.crear(request))
				.hasMessageContaining("proveedor no esta activo");
	}

	private Proveedor proveedorActivo(UUID id) {
		Proveedor proveedor = new Proveedor("Proveedor Uno", "20609998881", "987654321", "014700000", "ventas@uno.pe", "Av. Uno 123");
		ReflectionTestUtils.setField(proveedor, "id", id);
		return proveedor;
	}
}
