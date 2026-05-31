package com.titishop.productos.entity;

import com.titishop.compartido.entity.AuditoriaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "categorias")
public class Categoria extends AuditoriaEntity {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(nullable = false, unique = true, length = 80)
	private String nombre;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoCatalogo estado = EstadoCatalogo.ACTIVO;

	protected Categoria() {
	}

	public Categoria(String nombre) {
		this.nombre = nombre;
		this.estado = EstadoCatalogo.ACTIVO;
	}

	public UUID getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public EstadoCatalogo getEstado() {
		return estado;
	}

	public void actualizar(String nombre, EstadoCatalogo estado) {
		this.nombre = nombre;
		this.estado = estado;
		setActualizadoEn(Instant.now());
	}

	public void inactivar() {
		this.estado = EstadoCatalogo.INACTIVO;
		setInactivadoEn(Instant.now());
		setActualizadoEn(Instant.now());
	}
}
