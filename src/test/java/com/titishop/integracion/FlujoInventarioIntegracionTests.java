package com.titishop.integracion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.titishop.inventario.dto.CrearInventarioRequest;
import com.titishop.inventario.dto.InventarioResponse;
import com.titishop.inventario.service.InventarioService;
import com.titishop.movimientos.dto.MovimientoResponse;
import com.titishop.movimientos.dto.RegistrarMovimientoRequest;
import com.titishop.movimientos.dto.TipoMovimiento;
import com.titishop.movimientos.exception.StockInsuficienteException;
import com.titishop.movimientos.service.MovimientoService;
import com.titishop.productos.dto.CrearCategoriaRequest;
import com.titishop.productos.dto.CrearMarcaRequest;
import com.titishop.productos.dto.CrearProductoRequest;
import com.titishop.productos.dto.ProductoResponse;
import com.titishop.productos.service.CategoriaService;
import com.titishop.productos.service.MarcaService;
import com.titishop.productos.service.ProductoService;
import com.titishop.proveedores.dto.CrearProveedorRequest;
import com.titishop.proveedores.dto.ProveedorResponse;
import com.titishop.proveedores.service.ProveedorService;
import com.titishop.usuarios.entity.Usuario;
import com.titishop.usuarios.repository.UsuarioRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:flujo-integracion;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.hibernate.ddl-auto=none",
		"spring.flyway.enabled=true"
})
class FlujoInventarioIntegracionTests {

	@Autowired
	private CategoriaService categoriaService;

	@Autowired
	private MarcaService marcaService;

	@Autowired
	private ProductoService productoService;

	@Autowired
	private ProveedorService proveedorService;

	@Autowired
	private InventarioService inventarioService;

	@Autowired
	private MovimientoService movimientoService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void flujoCompletoMantieneConsistenciaDeStockConMigracionesFlyway() {
		Usuario admin = usuarioRepository.findByEmailIgnoreCase("kevin@gmail.com").orElseThrow();
		assertThat(passwordEncoder.matches("kevin123", admin.getPasswordHash())).isTrue();
		var categoria = categoriaService.crear(new CrearCategoriaRequest("Integracion"));
		var marca = marcaService.crear(new CrearMarcaRequest("Marca Integracion"));
		ProductoResponse producto = productoService.crear(new CrearProductoRequest(
				"Producto Integracion",
				"SKU-INT-001",
				"Producto para prueba integrada",
				null,
				categoria.id(),
				marca.id(),
				BigDecimal.valueOf(10),
				BigDecimal.valueOf(15)
		));
		ProveedorResponse proveedor = proveedorService.crear(new CrearProveedorRequest(
				"Proveedor Integracion",
				"20609998881",
				"987654321",
				"014700000",
				"integracion@proveedor.pe",
				"Av. Integracion 123"
		));

		InventarioResponse inventario = inventarioService.crear(new CrearInventarioRequest(
				producto.id(),
				10,
				3,
				"A-INT"
		));
		assertThat(inventario.stockActual()).isEqualTo(10);

		MovimientoResponse entrada = movimientoService.registrar(new RegistrarMovimientoRequest(
				producto.id(),
				proveedor.id(),
				admin.getId(),
				TipoMovimiento.ENTRADA,
				5,
				null,
				"Reposicion integrada"
		));
		assertThat(entrada.stockAntes()).isEqualTo(10);
		assertThat(entrada.stockDespues()).isEqualTo(15);

		MovimientoResponse salida = movimientoService.registrar(new RegistrarMovimientoRequest(
				producto.id(),
				null,
				admin.getId(),
				TipoMovimiento.SALIDA,
				4,
				null,
				"Salida integrada"
		));
		assertThat(salida.stockAntes()).isEqualTo(15);
		assertThat(salida.stockDespues()).isEqualTo(11);

		assertThatThrownBy(() -> movimientoService.registrar(new RegistrarMovimientoRequest(
				producto.id(),
				null,
				admin.getId(),
				TipoMovimiento.SALIDA,
				12,
				null,
				"Salida invalida"
		))).isInstanceOf(StockInsuficienteException.class);
	}
}
