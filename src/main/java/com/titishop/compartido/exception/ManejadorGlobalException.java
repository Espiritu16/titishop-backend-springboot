package com.titishop.compartido.exception;

import com.titishop.compartido.response.ErrorResponse;
import com.titishop.inventario.exception.InventarioDuplicadoPorProductoException;
import com.titishop.inventario.exception.InventarioNoEncontradoException;
import com.titishop.inventario.exception.ProductoInactivoParaInventarioException;
import com.titishop.productos.exception.CategoriaNoEncontradaException;
import com.titishop.productos.exception.MarcaNoEncontradaException;
import com.titishop.productos.exception.NombreCategoriaDuplicadoException;
import com.titishop.productos.exception.NombreMarcaDuplicadoException;
import com.titishop.productos.exception.ProductoNoEncontradoException;
import com.titishop.productos.exception.SkuDuplicadoException;
import com.titishop.usuarios.exception.EmailUsuarioDuplicadoException;
import com.titishop.usuarios.exception.UsuarioNoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorGlobalException {

	@ExceptionHandler(UsuarioNoEncontradoException.class)
	ResponseEntity<ErrorResponse> manejarUsuarioNoEncontrado(UsuarioNoEncontradoException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(EmailUsuarioDuplicadoException.class)
	ResponseEntity<ErrorResponse> manejarEmailDuplicado(EmailUsuarioDuplicadoException ex, HttpServletRequest request) {
		return build(HttpStatus.CONFLICT, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler({CategoriaNoEncontradaException.class, ProductoNoEncontradoException.class, MarcaNoEncontradaException.class})
	ResponseEntity<ErrorResponse> manejarRecursoNoEncontrado(RuntimeException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(InventarioNoEncontradoException.class)
	ResponseEntity<ErrorResponse> manejarInventarioNoEncontrado(InventarioNoEncontradoException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler({SkuDuplicadoException.class, NombreCategoriaDuplicadoException.class, NombreMarcaDuplicadoException.class})
	ResponseEntity<ErrorResponse> manejarDuplicados(RuntimeException ex, HttpServletRequest request) {
		return build(HttpStatus.CONFLICT, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(InventarioDuplicadoPorProductoException.class)
	ResponseEntity<ErrorResponse> manejarInventarioDuplicado(InventarioDuplicadoPorProductoException ex, HttpServletRequest request) {
		return build(HttpStatus.CONFLICT, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(ProductoInactivoParaInventarioException.class)
	ResponseEntity<ErrorResponse> manejarProductoInactivoInventario(ProductoInactivoParaInventarioException ex, HttpServletRequest request) {
		return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request, List.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
		List<String> details = ex.getBindingResult().getFieldErrors().stream()
				.map(this::formatearErrorCampo)
				.toList();
		return build(HttpStatus.BAD_REQUEST, "Error de validacion.", request, details);
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<ErrorResponse> manejarGenerico(Exception ex, HttpServletRequest request) {
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor.", request, List.of());
	}

	private String formatearErrorCampo(FieldError fieldError) {
		String message = fieldError.getDefaultMessage() == null ? "valor invalido" : fieldError.getDefaultMessage();
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
