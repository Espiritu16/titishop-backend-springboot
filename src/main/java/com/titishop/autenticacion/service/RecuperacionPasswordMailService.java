package com.titishop.autenticacion.service;

public interface RecuperacionPasswordMailService {

	void enviarCodigo(String email, String nombreCompleto, String codigo);
}
