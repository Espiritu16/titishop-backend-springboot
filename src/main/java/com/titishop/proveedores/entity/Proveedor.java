package com.titishop.proveedores.entity;

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
@Table(name = "proveedores")
public class Proveedor extends AuditoriaEntity {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(name = "razon_social", nullable = false, length = 120)
	private String razonSocial;

	@Column(nullable = false, unique = true, length = 11)
	private String ruc;

	@Column(length = 9)
	private String celular;

	@Column(length = 9)
	private String telefono;

	@Column(unique = true, length = 160)
	private String email;

	@Column(nullable = false, length = 160)
	private String direccion;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoProveedor estado = EstadoProveedor.ACTIVO;

	public Proveedor() {
	}

	public Proveedor(String razonSocial, String ruc, String celular, String telefono, String email, String direccion) {
		this.razonSocial = razonSocial;
		this.ruc = ruc;
		this.celular = celular;
		this.telefono = telefono;
		this.email = email;
		this.direccion = direccion;
		this.estado = EstadoProveedor.ACTIVO;
	}

	public UUID getId() {
		return id;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public String getRuc() {
		return ruc;
	}

	public String getCelular() {
		return celular;
	}

	public String getTelefono() {
		return telefono;
	}

	public String getEmail() {
		return email;
	}

	public String getDireccion() {
		return direccion;
	}

	public EstadoProveedor getEstado() {
		return estado;
	}

	public void actualizar(
			String razonSocial,
			String ruc,
			String celular,
			String telefono,
			String email,
			String direccion,
			EstadoProveedor estado
	) {
		this.razonSocial = razonSocial;
		this.ruc = ruc;
		this.celular = celular;
		this.telefono = telefono;
		this.email = email;
		this.direccion = direccion;
		this.estado = estado;
		setActualizadoEn(Instant.now());
	}

	public void inactivar() {
		this.estado = EstadoProveedor.INACTIVO;
		setInactivadoEn(Instant.now());
		setActualizadoEn(Instant.now());
	}
}
