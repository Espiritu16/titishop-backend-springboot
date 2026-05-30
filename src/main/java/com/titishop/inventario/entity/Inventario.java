package com.titishop.inventario.entity;

import com.titishop.compartido.entity.AuditoriaEntity;
import com.titishop.productos.entity.Producto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "inventario")
public class Inventario extends AuditoriaEntity {

	@Id
	@GeneratedValue
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "producto_id", nullable = false, unique = true)
	private Producto producto;

	@Column(name = "stock_actual", nullable = false)
	private Integer stockActual = 0;

	@Column(name = "stock_minimo", nullable = false)
	private Integer stockMinimo = 0;

	@Column(nullable = false, length = 40)
	private String ubicacion;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoInventario estado = EstadoInventario.ACTIVO;
}
