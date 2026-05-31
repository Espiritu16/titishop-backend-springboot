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
import java.time.Instant;
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

	protected Inventario() {
	}

	public Inventario(Producto producto, Integer stockActual, Integer stockMinimo, String ubicacion) {
		this.producto = producto;
		this.stockActual = stockActual;
		this.stockMinimo = stockMinimo;
		this.ubicacion = ubicacion;
		this.estado = EstadoInventario.ACTIVO;
	}

	public UUID getId() {
		return id;
	}

	public Producto getProducto() {
		return producto;
	}

	public Integer getStockActual() {
		return stockActual;
	}

	public Integer getStockMinimo() {
		return stockMinimo;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public EstadoInventario getEstado() {
		return estado;
	}

	public void actualizar(Integer stockMinimo, String ubicacion, EstadoInventario estado) {
		this.stockMinimo = stockMinimo;
		this.ubicacion = ubicacion;
		this.estado = estado;
		setActualizadoEn(Instant.now());
	}

	public void inactivar() {
		this.estado = EstadoInventario.INACTIVO;
		setInactivadoEn(Instant.now());
		setActualizadoEn(Instant.now());
	}

	public boolean esStockCritico() {
		return stockActual <= stockMinimo;
	}
}
