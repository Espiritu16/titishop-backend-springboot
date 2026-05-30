package com.titishop.productos.entity;

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
@Table(name = "marcas")
public class Marca extends AuditoriaEntity {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(nullable = false, unique = true, length = 80)
	private String nombre;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoCatalogo estado = EstadoCatalogo.ACTIVO;
}
