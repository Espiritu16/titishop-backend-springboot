package com.titishop.proveedores.entity;

import com.titishop.compartido.entity.AuditoriaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

	@Column(nullable = false, length = 9)
	private String celular;

	@Column(nullable = false, length = 9)
	private String telefono;

	@Column(nullable = false, unique = true, length = 160)
	private String email;

	@Column(nullable = false, length = 160)
	private String direccion;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoProveedor estado = EstadoProveedor.ACTIVO;
}
