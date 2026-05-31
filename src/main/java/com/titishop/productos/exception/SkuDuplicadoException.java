package com.titishop.productos.exception;

public class SkuDuplicadoException extends RuntimeException {

	public SkuDuplicadoException(String sku) {
		super("El SKU ya se encuentra registrado: " + sku);
	}
}
