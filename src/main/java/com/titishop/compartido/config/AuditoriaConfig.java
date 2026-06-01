package com.titishop.compartido.config;

import com.titishop.compartido.entity.AuditoriaEntityListener;
import com.titishop.compartido.service.AuditoriaUsuarioActualProvider;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditoriaConfig {

	private final AuditoriaUsuarioActualProvider usuarioActualProvider;

	public AuditoriaConfig(AuditoriaUsuarioActualProvider usuarioActualProvider) {
		this.usuarioActualProvider = usuarioActualProvider;
	}

	@PostConstruct
	void configurarAuditoria() {
		AuditoriaEntityListener.configurarProveedor(usuarioActualProvider::obtener);
	}
}
