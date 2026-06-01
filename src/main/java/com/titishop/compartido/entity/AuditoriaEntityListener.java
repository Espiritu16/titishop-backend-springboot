package com.titishop.compartido.entity;

import com.titishop.usuarios.entity.Usuario;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

public class AuditoriaEntityListener {

	private static Supplier<Optional<Usuario>> usuarioActualProvider = Optional::empty;

	private final Supplier<Optional<Usuario>> provider;

	public AuditoriaEntityListener() {
		this(() -> usuarioActualProvider.get());
	}

	public AuditoriaEntityListener(Supplier<Optional<Usuario>> provider) {
		this.provider = provider;
	}

	public static void configurarProveedor(Supplier<Optional<Usuario>> provider) {
		usuarioActualProvider = provider == null ? Optional::empty : provider;
	}

	@PrePersist
	public void antesCrear(AuditoriaEntity entity) {
		if (entity.getCreadoEn() == null) {
			entity.setCreadoEn(Instant.now());
		}
		provider.get().ifPresent(usuario -> {
			if (entity.getCreadoPor() == null) {
				entity.setCreadoPor(usuario);
			}
		});
	}

	@PreUpdate
	public void antesActualizar(AuditoriaEntity entity) {
		entity.setActualizadoEn(Instant.now());
		provider.get().ifPresent(usuario -> {
			entity.setActualizadoPor(usuario);
			if (entity.getInactivadoEn() != null && entity.getInactivadoPor() == null) {
				entity.setInactivadoPor(usuario);
			}
		});
	}
}
