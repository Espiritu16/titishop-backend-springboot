package com.titishop.compartido.entity;

import com.titishop.usuarios.entity.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;

@MappedSuperclass
public abstract class AuditoriaEntity {

	@Column(name = "creado_en", nullable = false)
	private Instant creadoEn = Instant.now();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creado_por_id")
	private Usuario creadoPor;

	@Column(name = "actualizado_en")
	private Instant actualizadoEn;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "actualizado_por_id")
	private Usuario actualizadoPor;

	@Column(name = "inactivado_en")
	private Instant inactivadoEn;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inactivado_por_id")
	private Usuario inactivadoPor;

	public Instant getCreadoEn() {
		return creadoEn;
	}

	public Usuario getCreadoPor() {
		return creadoPor;
	}

	public Instant getActualizadoEn() {
		return actualizadoEn;
	}

	public Usuario getActualizadoPor() {
		return actualizadoPor;
	}

	public Instant getInactivadoEn() {
		return inactivadoEn;
	}

	public Usuario getInactivadoPor() {
		return inactivadoPor;
	}

	public void setActualizadoEn(Instant actualizadoEn) {
		this.actualizadoEn = actualizadoEn;
	}

	public void setInactivadoEn(Instant inactivadoEn) {
		this.inactivadoEn = inactivadoEn;
	}
}
