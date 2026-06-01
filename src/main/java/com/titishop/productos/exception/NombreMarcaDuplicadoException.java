package com.titishop.productos.exception;

public class NombreMarcaDuplicadoException extends RuntimeException {

	public NombreMarcaDuplicadoException(String nombre) {
		super("El nombre de marca ya se encuentra registrado: " + nombre);
	}
}
