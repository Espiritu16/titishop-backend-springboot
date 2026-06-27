package com.titishop.movimientos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.titishop.inventario.entity.Inventario;
import com.titishop.inventario.repository.InventarioRepository;
import com.titishop.movimientos.dto.MovimientoResponse;
import com.titishop.movimientos.dto.RegistrarMovimientoRequest;
import com.titishop.movimientos.dto.TipoMovimiento;
import com.titishop.movimientos.entity.Movimiento;
import com.titishop.movimientos.exception.MovimientoInvalidoException;
import com.titishop.movimientos.exception.MovimientoNoEncontradoException;
import com.titishop.movimientos.exception.ProveedorInactivoParaEntradaException;
import com.titishop.movimientos.exception.StockInsuficienteException;
import com.titishop.movimientos.repository.MovimientoRepository;
import com.titishop.movimientos.service.MovimientoService;
import com.titishop.productos.entity.Categoria;
import com.titishop.productos.entity.Marca;
import com.titishop.productos.entity.Producto;
import com.titishop.productos.repository.ProductoRepository;
import com.titishop.proveedores.entity.EstadoProveedor;
import com.titishop.proveedores.entity.Proveedor;
import com.titishop.proveedores.repository.ProveedorRepository;
import com.titishop.usuarios.entity.RolUsuario;
import com.titishop.usuarios.entity.Usuario;
import com.titishop.usuarios.repository.UsuarioRepository;
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
class MovimientoServiceTests {

	@Mock
	private MovimientoRepository movimientoRepository;
	@Mock
	private ProductoRepository productoRepository;
	@Mock
	private InventarioRepository inventarioRepository;
	@Mock
	private ProveedorRepository proveedorRepository;
	@Mock
	private UsuarioRepository usuarioRepository;

	private MovimientoService movimientoService;

	@BeforeEach
	void setUp() {
		movimientoService = new MovimientoService(
				movimientoRepository,
				productoRepository,
				inventarioRepository,
				proveedorRepository,
				usuarioRepository
		);
	}

	@Test
	void registrarEntradaIncrementaStockYGuardaMovimiento() {
		UUID productoId = UUID.randomUUID();
		UUID proveedorId = UUID.randomUUID();
		UUID usuarioId = UUID.randomUUID();
		Producto producto = productoActivo(productoId);
		Proveedor proveedor = proveedorActivo(proveedorId);
		Usuario usuario = usuarioActivo(usuarioId);
		Inventario inventario = new Inventario(producto, 10, 3, "A-01");
		RegistrarMovimientoRequest request = new RegistrarMovimientoRequest(
				productoId, proveedorId, usuarioId, TipoMovimiento.ENTRADA, 5, null, "Reposicion"
		);

		when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
		when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(inventario));
		when(proveedorRepository.findById(proveedorId)).thenReturn(Optional.of(proveedor));
		when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
		when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(invocation -> invocation.getArgument(0));

		MovimientoResponse response = movimientoService.registrar(request);

		assertThat(inventario.getStockActual()).isEqualTo(15);
		assertThat(response.stockAntes()).isEqualTo(10);
		assertThat(response.stockDespues()).isEqualTo(15);
		assertThat(response.proveedorId()).isEqualTo(proveedorId);
		verify(inventarioRepository).save(inventario);
		verify(movimientoRepository).save(any(Movimiento.class));
	}

	@Test
	void registrarSalidaFallaSiDejaStockNegativo() {
		UUID productoId = UUID.randomUUID();
		UUID usuarioId = UUID.randomUUID();
		Producto producto = productoActivo(productoId);
		Inventario inventario = new Inventario(producto, 4, 3, "A-01");
		RegistrarMovimientoRequest request = new RegistrarMovimientoRequest(
				productoId, null, usuarioId, TipoMovimiento.SALIDA, 5, null, "Venta"
		);

		when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
		when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(inventario));

		assertThatThrownBy(() -> movimientoService.registrar(request))
				.isInstanceOf(StockInsuficienteException.class);
		verify(movimientoRepository, never()).save(any(Movimiento.class));
	}

	@Test
	void registrarEntradaFallaSiProveedorEstaInactivo() {
		UUID productoId = UUID.randomUUID();
		UUID proveedorId = UUID.randomUUID();
		UUID usuarioId = UUID.randomUUID();
		Producto producto = productoActivo(productoId);
		Proveedor proveedor = proveedorActivo(proveedorId);
		ReflectionTestUtils.setField(proveedor, "estado", EstadoProveedor.INACTIVO);
		Inventario inventario = new Inventario(producto, 10, 3, "A-01");
		RegistrarMovimientoRequest request = new RegistrarMovimientoRequest(
				productoId, proveedorId, usuarioId, TipoMovimiento.ENTRADA, 5, null, "Reposicion"
		);

		when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
		when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(inventario));
		when(proveedorRepository.findById(proveedorId)).thenReturn(Optional.of(proveedor));

		assertThatThrownBy(() -> movimientoService.registrar(request))
				.isInstanceOf(ProveedorInactivoParaEntradaException.class);
	}

	@Test
	void registrarAjusteUsaStockDestino() {
		UUID productoId = UUID.randomUUID();
		UUID usuarioId = UUID.randomUUID();
		Producto producto = productoActivo(productoId);
		Usuario usuario = usuarioActivo(usuarioId);
		Inventario inventario = new Inventario(producto, 12, 3, "A-01");
		RegistrarMovimientoRequest request = new RegistrarMovimientoRequest(
				productoId, null, usuarioId, TipoMovimiento.AJUSTE, null, 8, "Conteo fisico"
		);

		when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
		when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(inventario));
		when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
		when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(invocation -> invocation.getArgument(0));

		MovimientoResponse response = movimientoService.registrar(request);

		assertThat(inventario.getStockActual()).isEqualTo(8);
		assertThat(response.cantidad()).isEqualTo(4);
		assertThat(response.stockAntes()).isEqualTo(12);
		assertThat(response.stockDespues()).isEqualTo(8);
	}

	@Test
	void registrarEntradaFallaSiCantidadSuperaMaximoPermitido() {
		UUID productoId = UUID.randomUUID();
		UUID proveedorId = UUID.randomUUID();
		UUID usuarioId = UUID.randomUUID();
		RegistrarMovimientoRequest request = new RegistrarMovimientoRequest(
				productoId, proveedorId, usuarioId, TipoMovimiento.ENTRADA, 1001, null, "Reposicion"
		);

		assertThatThrownBy(() -> movimientoService.registrar(request))
				.isInstanceOf(MovimientoInvalidoException.class)
				.hasMessage("La cantidad debe estar entre 1 y 1000.");
		verify(movimientoRepository, never()).save(any(Movimiento.class));
	}

	@Test
	void registrarAjusteFallaSiStockDestinoSuperaMaximoPermitido() {
		UUID productoId = UUID.randomUUID();
		UUID usuarioId = UUID.randomUUID();
		RegistrarMovimientoRequest request = new RegistrarMovimientoRequest(
				productoId, null, usuarioId, TipoMovimiento.AJUSTE, null, 1001, "Conteo fisico"
		);

		assertThatThrownBy(() -> movimientoService.registrar(request))
				.isInstanceOf(MovimientoInvalidoException.class)
				.hasMessage("El stock destino debe estar entre 1 y 1000.");
		verify(movimientoRepository, never()).save(any(Movimiento.class));
	}

	@Test
	void anularEntradaRevierteStock() {
		UUID movimientoId = UUID.randomUUID();
		UUID usuarioId = UUID.randomUUID();
		Producto producto = productoActivo(UUID.randomUUID());
		Usuario creador = usuarioActivo(UUID.randomUUID());
		Usuario anulador = usuarioActivo(usuarioId);
		Inventario inventario = new Inventario(producto, 15, 3, "A-01");
		Movimiento movimiento = new Movimiento(
				producto,
				null,
				com.titishop.movimientos.entity.TipoMovimiento.ENTRADA,
				5,
				"Reposicion",
				10,
				15,
				creador
		);

		when(movimientoRepository.findById(movimientoId)).thenReturn(Optional.of(movimiento));
		when(inventarioRepository.findByProductoId(producto.getId())).thenReturn(Optional.of(inventario));
		when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(anulador));

		movimientoService.anular(movimientoId, usuarioId, "Error de registro");

		assertThat(inventario.getStockActual()).isEqualTo(10);
		assertThat(movimiento.getAnuladoEn()).isNotNull();
		verify(inventarioRepository).save(inventario);
		verify(movimientoRepository).save(movimiento);
	}

	@Test
	void anularFallaSiMovimientoNoExiste() {
		UUID movimientoId = UUID.randomUUID();
		when(movimientoRepository.findById(movimientoId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> movimientoService.anular(movimientoId, UUID.randomUUID(), "Error"))
				.isInstanceOf(MovimientoNoEncontradoException.class);
	}

	private Producto productoActivo(UUID id) {
		Categoria categoria = new Categoria("Perifericos");
		Marca marca = new Marca();
		ReflectionTestUtils.setField(marca, "id", UUID.randomUUID());
		ReflectionTestUtils.setField(marca, "nombre", "Logi");
		Producto producto = new Producto("Mouse", "SKU-MOV", "Optico", null, categoria, marca, BigDecimal.ONE, BigDecimal.TEN);
		ReflectionTestUtils.setField(producto, "id", id);
		return producto;
	}

	private Proveedor proveedorActivo(UUID id) {
		Proveedor proveedor = new Proveedor("Proveedor Uno", "20609998881", "987654321", "014700000", "ventas@uno.pe", "Av. Uno 123");
		ReflectionTestUtils.setField(proveedor, "id", id);
		return proveedor;
	}

	private Usuario usuarioActivo(UUID id) {
		Usuario usuario = new Usuario("Admin Uno", "admin@titishop.pe", "hash", RolUsuario.ADMINISTRADOR);
		ReflectionTestUtils.setField(usuario, "id", id);
		return usuario;
	}
}
