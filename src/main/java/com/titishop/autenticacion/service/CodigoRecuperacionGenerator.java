package com.titishop.autenticacion.service;

public interface CodigoRecuperacionGenerator {

	String generarCodigo();

	String generarResetToken();
}
