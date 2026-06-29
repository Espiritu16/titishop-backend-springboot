package com.titishop.exportaciones.controller;

import com.titishop.exportaciones.service.ArchivoExportacion;
import com.titishop.exportaciones.service.ColumnaExportacion;
import com.titishop.exportaciones.service.ExportacionService;
import com.titishop.exportaciones.service.FormatoExportacion;
import com.titishop.inventario.dto.EstadoInventario;
import com.titishop.inventario.dto.InventarioResponse;
import com.titishop.inventario.service.InventarioService;
import com.titishop.movimientos.dto.MovimientoResponse;
import com.titishop.movimientos.dto.TipoMovimiento;
import com.titishop.movimientos.service.MovimientoService;
import com.titishop.productos.dto.EstadoProducto;
import com.titishop.productos.dto.ProductoResponse;
import com.titishop.productos.service.ProductoService;
import com.titishop.proveedores.dto.EstadoProveedor;
import com.titishop.proveedores.dto.ProveedorResponse;
import com.titishop.proveedores.service.ProveedorService;
import com.titishop.usuarios.dto.UsuarioResponse;
import com.titishop.usuarios.entity.EstadoUsuario;
import com.titishop.usuarios.entity.RolUsuario;
import com.titishop.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/exportaciones")
@Tag(name = "Exportaciones", description = "Descarga de datos filtrados en Excel y PDF.")
@SecurityRequirement(name = "bearerAuth")
public class ExportacionController {

	private final ExportacionService exportacionService;
	private final ProductoService productoService;
	private final ProveedorService proveedorService;
	private final InventarioService inventarioService;
	private final MovimientoService movimientoService;
	private final UsuarioService usuarioService;

	public ExportacionController(
			ExportacionService exportacionService,
			ProductoService productoService,
			ProveedorService proveedorService,
			InventarioService inventarioService,
			MovimientoService movimientoService,
			UsuarioService usuarioService
	) {
		this.exportacionService = exportacionService;
		this.productoService = productoService;
		this.proveedorService = proveedorService;
		this.inventarioService = inventarioService;
		this.movimientoService = movimientoService;
		this.usuarioService = usuarioService;
	}

	@GetMapping("/productos/{formato}")
	public ResponseEntity<byte[]> productos(
			@PathVariable String formato,
			@RequestParam(required = false) String busqueda,
			@RequestParam(required = false) EstadoProducto estado,
			@RequestParam(required = false) UUID categoriaId,
			@RequestParam(required = false) UUID marcaId,
			@RequestParam(required = false) UUID proveedorId
	) {
		return descargar(exportacionService.exportar(
				"Productos",
				"productos",
				FormatoExportacion.desde(formato),
				(page, size) -> productoService.listar(page, size, busqueda, estado, categoriaId, marcaId, proveedorId),
				List.of(
						new ColumnaExportacion<>("Nombre", ProductoResponse::nombre),
						new ColumnaExportacion<>("SKU", ProductoResponse::sku),
						new ColumnaExportacion<>("Descripcion", ProductoResponse::descripcion),
						new ColumnaExportacion<>("Categoria", ProductoResponse::categoriaNombre),
						new ColumnaExportacion<>("Marca", ProductoResponse::marcaNombre),
						new ColumnaExportacion<>("Proveedor", ProductoResponse::proveedorRazonSocial),
						new ColumnaExportacion<>("Pais origen", ProductoResponse::paisOrigen),
						new ColumnaExportacion<>("Precio compra", ProductoResponse::precioCompra),
						new ColumnaExportacion<>("Precio venta", ProductoResponse::precioVenta),
						new ColumnaExportacion<>("Estado", ProductoResponse::estado)
				)
		));
	}

	@GetMapping("/proveedores/{formato}")
	public ResponseEntity<byte[]> proveedores(
			@PathVariable String formato,
			@RequestParam(required = false) String busqueda,
			@RequestParam(required = false) EstadoProveedor estado
	) {
		return descargar(exportacionService.exportar(
				"Proveedores",
				"proveedores",
				FormatoExportacion.desde(formato),
				(page, size) -> proveedorService.listar(page, size, busqueda, estado),
				List.of(
						new ColumnaExportacion<>("Razon social", ProveedorResponse::razonSocial),
						new ColumnaExportacion<>("RUC", ProveedorResponse::ruc),
						new ColumnaExportacion<>("Celular", ProveedorResponse::celular),
						new ColumnaExportacion<>("Telefono", ProveedorResponse::telefono),
						new ColumnaExportacion<>("Correo", ProveedorResponse::email),
						new ColumnaExportacion<>("Direccion", ProveedorResponse::direccion),
						new ColumnaExportacion<>("Estado", ProveedorResponse::estado)
				)
		));
	}

	@GetMapping("/inventario/{formato}")
	public ResponseEntity<byte[]> inventario(
			@PathVariable String formato,
			@RequestParam(required = false) String busqueda,
			@RequestParam(required = false) EstadoInventario estado,
			@RequestParam(required = false) String stockEstado
	) {
		return descargar(exportacionService.exportar(
				"Inventario",
				"inventario",
				FormatoExportacion.desde(formato),
				(page, size) -> inventarioService.listar(page, size, busqueda, estado, stockEstado),
				List.of(
						new ColumnaExportacion<>("Producto", InventarioResponse::productoNombre),
						new ColumnaExportacion<>("SKU", InventarioResponse::productoSku),
						new ColumnaExportacion<>("Stock actual", InventarioResponse::stockActual),
						new ColumnaExportacion<>("Stock minimo", InventarioResponse::stockMinimo),
						new ColumnaExportacion<>("Ubicacion", InventarioResponse::ubicacion),
						new ColumnaExportacion<>("Estado", InventarioResponse::estado),
						new ColumnaExportacion<>("Stock critico", item -> item.stockCritico() ? "SI" : "NO")
				)
		));
	}

	@GetMapping("/movimientos/{formato}")
	public ResponseEntity<byte[]> movimientos(
			@PathVariable String formato,
			@RequestParam(required = false) String busqueda,
			@RequestParam(required = false) TipoMovimiento tipo,
			@RequestParam(required = false) Boolean anulado,
			@RequestParam(required = false) UUID productoId
	) {
		return descargar(exportacionService.exportar(
				"Movimientos",
				"movimientos",
				FormatoExportacion.desde(formato),
				(page, size) -> movimientoService.listar(page, size, busqueda, tipo, anulado, productoId),
				List.of(
						new ColumnaExportacion<>("Tipo", MovimientoResponse::tipo),
						new ColumnaExportacion<>("Producto", MovimientoResponse::productoNombre),
						new ColumnaExportacion<>("SKU", MovimientoResponse::productoSku),
						new ColumnaExportacion<>("Proveedor", MovimientoResponse::proveedorRazonSocial),
						new ColumnaExportacion<>("Cantidad", MovimientoResponse::cantidad),
						new ColumnaExportacion<>("Stock antes", MovimientoResponse::stockAntes),
						new ColumnaExportacion<>("Stock despues", MovimientoResponse::stockDespues),
						new ColumnaExportacion<>("Motivo", MovimientoResponse::motivo),
						new ColumnaExportacion<>("Usuario", MovimientoResponse::creadoPorNombre),
						new ColumnaExportacion<>("Estado", item -> item.anuladoEn() == null ? "VIGENTE" : "ANULADO")
				)
		));
	}

	@GetMapping("/usuarios/{formato}")
	public ResponseEntity<byte[]> usuarios(
			@PathVariable String formato,
			@RequestParam(required = false) String busqueda,
			@RequestParam(required = false) RolUsuario rol,
			@RequestParam(required = false) EstadoUsuario estado
	) {
		return descargar(exportacionService.exportar(
				"Usuarios",
				"usuarios",
				FormatoExportacion.desde(formato),
				(page, size) -> usuarioService.listar(page, size, busqueda, rol, estado),
				List.of(
						new ColumnaExportacion<>("Nombre", UsuarioResponse::nombreCompleto),
						new ColumnaExportacion<>("Correo", UsuarioResponse::email),
						new ColumnaExportacion<>("Rol", UsuarioResponse::rol),
						new ColumnaExportacion<>("Estado", UsuarioResponse::estado),
						new ColumnaExportacion<>("Creado", UsuarioResponse::creadoEn)
				)
		));
	}

	private ResponseEntity<byte[]> descargar(ArchivoExportacion archivo) {
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
						.filename(archivo.nombreArchivo())
						.build()
						.toString())
				.contentType(MediaType.parseMediaType(archivo.mediaType()))
				.body(archivo.contenido());
	}
}
