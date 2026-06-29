package com.titishop.productos.controller;

import com.titishop.productos.dto.ActualizarProductoRequest;
import com.titishop.productos.dto.ActualizarEstadoProductoRequest;
import com.titishop.productos.dto.CrearProductoRequest;
import com.titishop.productos.dto.EstadoProducto;
import com.titishop.productos.dto.ProductoResponse;
import com.titishop.productos.service.ProductoService;
import com.titishop.compartido.response.ErrorResponse;
import com.titishop.compartido.response.PaginaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Gestion de productos del catalogo.")
@SecurityRequirement(name = "bearerAuth")
public class ProductoController {

	private final ProductoService productoService;

	public ProductoController(ProductoService productoService) {
		this.productoService = productoService;
	}

	@GetMapping
	@Operation(summary = "Listar productos", description = "Obtiene todos los productos registrados con sus referencias de categoria y marca.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Listado obtenido correctamente.",
					content = @Content(schema = @Schema(implementation = PaginaResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public PaginaResponse<ProductoResponse> listar(
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
			@RequestParam(required = false) String busqueda,
			@RequestParam(required = false) EstadoProducto estado,
			@RequestParam(required = false) UUID categoriaId,
			@RequestParam(required = false) UUID marcaId,
			@RequestParam(required = false) UUID proveedorId
	) {
		return productoService.listar(page, size, busqueda, estado, categoriaId, marcaId, proveedorId);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener producto por ID", description = "Busca un producto especifico por su identificador.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Producto encontrado.",
					content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Producto no encontrado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ProductoResponse obtenerPorId(@Parameter(description = "ID del producto.", example = "4f55a1cc-a3f8-4be1-83ab-9b9f4c9c1001") @PathVariable UUID id) {
		return productoService.obtenerPorId(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Crear producto", description = "Registra un nuevo producto en el catalogo.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			description = "Datos del producto a registrar.",
			content = @Content(
					schema = @Schema(implementation = CrearProductoRequest.class),
					examples = @ExampleObject(
							name = "Nuevo producto",
							value = """
									{
									  "nombre": "Leche Evaporada Entera 400g",
									  "sku": "LEC-400-001",
									  "descripcion": "Leche evaporada entera en lata de 400 gramos.",
									  "imagenUrl": "https://cdn.titishop.local/productos/leche-400g.png",
									  "categoriaId": "0f1e2d3c-4b5a-6789-9012-3456789abcde",
									  "marcaId": "1ab2cd34-56ef-7890-ab12-cd34ef567890",
									  "proveedorId": "5a81e2d0-55f8-4a3b-8d65-febec9959002",
									  "paisOrigen": "China",
									  "precioCompra": 3.20,
									  "precioVenta": 4.50
									}
									"""
					)
			)
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Producto creado correctamente.",
					content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
			@ApiResponse(responseCode = "400", description = "Datos de entrada invalidos.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Categoria o marca no encontrada.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Ya existe un producto con el SKU enviado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "422", description = "Categoria o marca inactiva para el producto.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ProductoResponse crear(@Valid @RequestBody CrearProductoRequest request) {
		return productoService.crear(request);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar producto", description = "Actualiza la informacion comercial de un producto existente.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			description = "Datos actualizados del producto.",
			content = @Content(
					schema = @Schema(implementation = ActualizarProductoRequest.class),
					examples = @ExampleObject(
							name = "Actualizar producto",
							value = """
									{
									  "nombre": "Leche Evaporada Entera 410g",
									  "sku": "LEC-410-001",
									  "descripcion": "Leche evaporada entera en lata de 410 gramos.",
									  "imagenUrl": "https://cdn.titishop.local/productos/leche-410g.png",
									  "categoriaId": "0f1e2d3c-4b5a-6789-9012-3456789abcde",
									  "marcaId": "1ab2cd34-56ef-7890-ab12-cd34ef567890",
									  "proveedorId": "5a81e2d0-55f8-4a3b-8d65-febec9959002",
									  "paisOrigen": "China",
									  "precioCompra": 3.30,
									  "precioVenta": 4.80,
									  "estado": "ACTIVO"
									}
									"""
					)
			)
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Producto actualizado correctamente.",
					content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
			@ApiResponse(responseCode = "400", description = "Datos de entrada invalidos.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Producto, categoria o marca no encontrada.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Ya existe un producto con el SKU enviado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "422", description = "Categoria o marca inactiva para el producto.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ProductoResponse actualizar(
			@Parameter(description = "ID del producto a actualizar.", example = "4f55a1cc-a3f8-4be1-83ab-9b9f4c9c1001")
			@PathVariable UUID id,
			@Valid @RequestBody ActualizarProductoRequest request
	) {
		return productoService.actualizar(id, request);
	}

	@PatchMapping("/{id}/estado")
	@Operation(summary = "Cambiar estado de producto", description = "Actualiza solo el estado comercial de un producto.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Estado actualizado correctamente.",
					content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
			@ApiResponse(responseCode = "400", description = "Datos de entrada invalidos.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Producto no encontrado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "422", description = "Categoria o marca inactiva para activar el producto.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ProductoResponse actualizarEstado(
			@Parameter(description = "ID del producto.", example = "4f55a1cc-a3f8-4be1-83ab-9b9f4c9c1001")
			@PathVariable UUID id,
			@Valid @RequestBody ActualizarEstadoProductoRequest request
	) {
		return productoService.actualizarEstado(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Inactivar producto", description = "Marca un producto como inactivo sin eliminarlo.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Producto inactivado correctamente."),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "404", description = "Producto no encontrado.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public void inactivar(@Parameter(description = "ID del producto a inactivar.", example = "4f55a1cc-a3f8-4be1-83ab-9b9f4c9c1001") @PathVariable UUID id) {
		productoService.inactivar(id);
	}
}
