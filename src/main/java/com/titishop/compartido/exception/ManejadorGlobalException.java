package com.titishop.compartido.exception;

import com.titishop.archivos.exception.ArchivoInvalidoException;
import com.titishop.archivos.exception.ArchivoStorageException;
import com.titishop.autenticacion.exception.RecuperacionPasswordInvalidaException;
import com.titishop.compartido.response.ErrorResponse;
import com.titishop.exportaciones.exception.ExportacionSinDatosException;
import com.titishop.exportaciones.exception.FormatoExportacionInvalidoException;
import com.titishop.inventario.exception.InventarioDuplicadoPorProductoException;
import com.titishop.inventario.exception.InventarioInvalidoException;
import com.titishop.inventario.exception.InventarioNoEncontradoException;
import com.titishop.inventario.exception.ProductoInactivoParaInventarioException;
import com.titishop.movimientos.exception.MovimientoInvalidoException;
import com.titishop.movimientos.exception.MovimientoNoEncontradoException;
import com.titishop.movimientos.exception.MovimientoYaAnuladoException;
import com.titishop.movimientos.exception.ProveedorInactivoParaEntradaException;
import com.titishop.movimientos.exception.ProveedorRequeridoParaEntradaException;
import com.titishop.movimientos.exception.StockInsuficienteException;
import com.titishop.productos.exception.CategoriaNoEncontradaException;
import com.titishop.productos.exception.CategoriaInactivaParaProductoException;
import com.titishop.productos.exception.MarcaInactivaParaProductoException;
import com.titishop.productos.exception.MarcaNoEncontradaException;
import com.titishop.productos.exception.NombreCategoriaDuplicadoException;
import com.titishop.productos.exception.NombreMarcaDuplicadoException;
import com.titishop.productos.exception.ProductoInvalidoException;
import com.titishop.productos.exception.ProductoNoEncontradoException;
import com.titishop.productos.exception.ProveedorInactivoParaProductoException;
import com.titishop.productos.exception.SkuDuplicadoException;
import com.titishop.proveedores.exception.EmailProveedorDuplicadoException;
import com.titishop.proveedores.exception.FactilizaDocumentoNoEncontradoException;
import com.titishop.proveedores.exception.FactilizaServicioNoDisponibleException;
import com.titishop.proveedores.exception.ProveedorNoEncontradoException;
import com.titishop.proveedores.exception.RucProveedorDuplicadoException;
import com.titishop.usuarios.exception.EmailUsuarioDuplicadoException;
import com.titishop.usuarios.exception.UsuarioNoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ManejadorGlobalException {

	private static final Logger log = LoggerFactory.getLogger(ManejadorGlobalException.class);

	@ExceptionHandler(UsuarioNoEncontradoException.class)
	ResponseEntity<ErrorResponse> manejarUsuarioNoEncontrado(UsuarioNoEncontradoException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(EmailUsuarioDuplicadoException.class)
	ResponseEntity<ErrorResponse> manejarEmailDuplicado(EmailUsuarioDuplicadoException ex, HttpServletRequest request) {
		return build(HttpStatus.CONFLICT, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler({
			CategoriaNoEncontradaException.class,
			ProductoNoEncontradoException.class,
			MarcaNoEncontradaException.class,
			ProveedorNoEncontradoException.class,
			MovimientoNoEncontradoException.class,
			FactilizaDocumentoNoEncontradoException.class
	})
	ResponseEntity<ErrorResponse> manejarRecursoNoEncontrado(RuntimeException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(InventarioNoEncontradoException.class)
	ResponseEntity<ErrorResponse> manejarInventarioNoEncontrado(InventarioNoEncontradoException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler({
			SkuDuplicadoException.class,
			NombreCategoriaDuplicadoException.class,
			NombreMarcaDuplicadoException.class,
			RucProveedorDuplicadoException.class,
			EmailProveedorDuplicadoException.class
	})
	ResponseEntity<ErrorResponse> manejarDuplicados(RuntimeException ex, HttpServletRequest request) {
		return build(HttpStatus.CONFLICT, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(FactilizaServicioNoDisponibleException.class)
	ResponseEntity<ErrorResponse> manejarFactilizaNoDisponible(FactilizaServicioNoDisponibleException ex, HttpServletRequest request) {
		return build(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(ArchivoStorageException.class)
	ResponseEntity<ErrorResponse> manejarArchivoStorage(ArchivoStorageException ex, HttpServletRequest request) {
		return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(InventarioDuplicadoPorProductoException.class)
	ResponseEntity<ErrorResponse> manejarInventarioDuplicado(InventarioDuplicadoPorProductoException ex, HttpServletRequest request) {
		return build(HttpStatus.CONFLICT, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(ProductoInactivoParaInventarioException.class)
	ResponseEntity<ErrorResponse> manejarProductoInactivoInventario(ProductoInactivoParaInventarioException ex, HttpServletRequest request) {
		return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler({
			ArchivoInvalidoException.class,
			CategoriaInactivaParaProductoException.class,
			InventarioInvalidoException.class,
			MarcaInactivaParaProductoException.class,
			MovimientoInvalidoException.class,
			MovimientoYaAnuladoException.class,
			ProductoInvalidoException.class,
			RecuperacionPasswordInvalidaException.class,
			ProveedorInactivoParaProductoException.class,
			ProveedorInactivoParaEntradaException.class,
			ProveedorRequeridoParaEntradaException.class,
			StockInsuficienteException.class
	})
	ResponseEntity<ErrorResponse> manejarReglaMovimiento(RuntimeException ex, HttpServletRequest request) {
		return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(SinCambiosException.class)
	ResponseEntity<ErrorResponse> manejarSinCambios(SinCambiosException ex, HttpServletRequest request) {
		return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(FormatoExportacionInvalidoException.class)
	ResponseEntity<ErrorResponse> manejarFormatoExportacionInvalido(FormatoExportacionInvalidoException ex, HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request, List.of("formato: valores permitidos excel, pdf"));
	}

	@ExceptionHandler(ExportacionSinDatosException.class)
	ResponseEntity<ErrorResponse> manejarExportacionSinDatos(ExportacionSinDatosException ex, HttpServletRequest request) {
		return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
		List<String> details = ex.getBindingResult().getFieldErrors().stream()
				.map(this::formatearErrorCampo)
				.toList();
		return build(HttpStatus.BAD_REQUEST, "Error de validación.", request, details);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	ResponseEntity<ErrorResponse> manejarViolacionRestriccion(ConstraintViolationException ex, HttpServletRequest request) {
		List<String> details = ex.getConstraintViolations().stream()
				.map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
				.toList();
		return build(HttpStatus.BAD_REQUEST, "Parámetro de solicitud inválido.", request, details);
	}

	@ExceptionHandler(AuthenticationException.class)
	ResponseEntity<ErrorResponse> manejarErrorAutenticacion(AuthenticationException ex, HttpServletRequest request) {
		return build(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos.", request, List.of());
	}

	@ExceptionHandler(AccessDeniedException.class)
	ResponseEntity<ErrorResponse> manejarAccesoDenegado(AccessDeniedException ex, HttpServletRequest request) {
		return build(HttpStatus.FORBIDDEN, "No tienes permisos para realizar esta acción.", request, List.of());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	ResponseEntity<ErrorResponse> manejarParametroTipoInvalido(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
		String detail = ex.getName() + ": valor inválido.";
		return build(HttpStatus.BAD_REQUEST, "Parámetro de solicitud inválido.", request, List.of(detail));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	ResponseEntity<ErrorResponse> manejarParametroFaltante(MissingServletRequestParameterException ex, HttpServletRequest request) {
		String detail = ex.getParameterName() + ": parámetro requerido.";
		return build(HttpStatus.BAD_REQUEST, "Parámetro de solicitud inválido.", request, List.of(detail));
	}

	@ExceptionHandler(MissingServletRequestPartException.class)
	ResponseEntity<ErrorResponse> manejarParteFaltante(MissingServletRequestPartException ex, HttpServletRequest request) {
		String detail = ex.getRequestPartName() + ": archivo requerido.";
		return build(HttpStatus.BAD_REQUEST, "Archivo requerido.", request, List.of(detail));
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	ResponseEntity<ErrorResponse> manejarArchivoExcesivo(MaxUploadSizeExceededException ex, HttpServletRequest request) {
		return build(HttpStatus.UNPROCESSABLE_ENTITY, "La imagen supera el tamaño máximo permitido.", request, List.of());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	ResponseEntity<ErrorResponse> manejarArgumentoInvalido(IllegalArgumentException ex, HttpServletRequest request) {
		List<String> details = ex.getMessage() == null || ex.getMessage().isBlank() ? List.of() : List.of(ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, "Parámetro de solicitud inválido.", request, details);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	ResponseEntity<ErrorResponse> manejarCuerpoNoLegible(HttpMessageNotReadableException ex, HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, "Cuerpo de solicitud inválido.", request, List.of());
	}

	@ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
	ResponseEntity<ErrorResponse> manejarTipoRespuestaNoAceptable(HttpMediaTypeNotAcceptableException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_ACCEPTABLE, "Tipo de respuesta no aceptable para este recurso.", request, List.of());
	}

	@ExceptionHandler(NoResourceFoundException.class)
	ResponseEntity<ErrorResponse> manejarRutaNoEncontrada(NoResourceFoundException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, "Ruta no encontrada.", request, List.of());
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<ErrorResponse> manejarGenerico(Exception ex, HttpServletRequest request) {
		log.error("Error interno no controlado en {} {}", request.getMethod(), request.getRequestURI(), ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor.", request, List.of());
	}

	private String formatearErrorCampo(FieldError fieldError) {
		String message = fieldError.getDefaultMessage() == null ? "valor inválido" : fieldError.getDefaultMessage();
		return fieldError.getField() + ": " + message;
	}

	private ResponseEntity<ErrorResponse> build(
			HttpStatus status,
			String message,
			HttpServletRequest request,
			List<String> details
	) {
		ErrorResponse response = new ErrorResponse(
				Instant.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				request.getRequestURI(),
				details
		);
		return ResponseEntity.status(status).body(response);
	}
}
