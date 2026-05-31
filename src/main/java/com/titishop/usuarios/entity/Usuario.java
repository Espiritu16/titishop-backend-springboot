package com.titishop.usuarios.entity;

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
@Table(name = "usuarios")
public class Usuario extends AuditoriaEntity {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(name = "nombre_completo", nullable = false, length = 120)
	private String nombreCompleto;

	@Column(nullable = false, unique = true, length = 160)
	private String email;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private RolUsuario rol;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoUsuario estado = EstadoUsuario.ACTIVO;

	protected Usuario() {
	}

	public Usuario(String nombreCompleto, String email, String passwordHash, RolUsuario rol) {
		this.nombreCompleto = nombreCompleto;
		this.email = email;
		this.passwordHash = passwordHash;
		this.rol = rol;
		this.estado = EstadoUsuario.ACTIVO;
	}

	public UUID getId() {
		return id;
	}

	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public RolUsuario getRol() {
		return rol;
	}

	public EstadoUsuario getEstado() {
		return estado;
	}

	public void actualizar(String nombreCompleto, String email, RolUsuario rol, EstadoUsuario estado) {
		this.nombreCompleto = nombreCompleto;
		this.email = email;
		this.rol = rol;
		this.estado = estado;
		setActualizadoEn(Instant.now());
	}

	public void actualizarPassword(String passwordHash) {
		this.passwordHash = passwordHash;
		setActualizadoEn(Instant.now());
	}

	public void inactivar() {
		this.estado = EstadoUsuario.INACTIVO;
		setInactivadoEn(Instant.now());
		setActualizadoEn(Instant.now());
	}
}
