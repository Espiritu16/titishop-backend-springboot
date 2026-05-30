package com.titishop.movimientos.entity;

import com.titishop.productos.entity.Producto;
import com.titishop.proveedores.entity.Proveedor;
import com.titishop.usuarios.entity.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "movimientos")
public class Movimiento {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "producto_id", nullable = false)
	private Producto producto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "proveedor_id")
	private Proveedor proveedor;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TipoMovimiento tipo;

	@Column(nullable = false)
	private Integer cantidad;

	@Column(nullable = false, length = 255)
	private String motivo;

	@Column(name = "stock_antes", nullable = false)
	private Integer stockAntes;

	@Column(name = "stock_despues", nullable = false)
	private Integer stockDespues;

	@Column(name = "creado_en", nullable = false)
	private Instant creadoEn = Instant.now();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creado_por_id", nullable = false)
	private Usuario creadoPor;

	@Column(name = "anulado_en")
	private Instant anuladoEn;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "anulado_por_id")
	private Usuario anuladoPor;

	@Column(name = "motivo_anulacion", length = 255)
	private String motivoAnulacion;
}
