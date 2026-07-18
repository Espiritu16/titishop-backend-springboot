package com.titishop.productos.exception;

public class NombreCategoriaDuplicadoException extends RuntimeException {

	public NombreCategoriaDuplicadoException(String nombre) {
		super("El nombre de categoría ya se encuentra registrado: " + nombre);
	}
}
