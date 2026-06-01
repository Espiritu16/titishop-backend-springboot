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

	protected Movimiento() {
	}

	public Movimiento(
			Producto producto,
			Proveedor proveedor,
			TipoMovimiento tipo,
			Integer cantidad,
			String motivo,
			Integer stockAntes,
			Integer stockDespues,
			Usuario creadoPor
	) {
		this.producto = producto;
		this.proveedor = proveedor;
		this.tipo = tipo;
		this.cantidad = cantidad;
		this.motivo = motivo;
		this.stockAntes = stockAntes;
		this.stockDespues = stockDespues;
		this.creadoPor = creadoPor;
		this.creadoEn = Instant.now();
	}

	public UUID getId() {
		return id;
	}

	public Producto getProducto() {
		return producto;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public TipoMovimiento getTipo() {
		return tipo;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public String getMotivo() {
		return motivo;
	}

	public Integer getStockAntes() {
		return stockAntes;
	}

	public Integer getStockDespues() {
		return stockDespues;
	}

	public Instant getCreadoEn() {
		return creadoEn;
	}

	public Usuario getCreadoPor() {
		return creadoPor;
	}

	public Instant getAnuladoEn() {
		return anuladoEn;
	}

	public Usuario getAnuladoPor() {
		return anuladoPor;
	}

	public String getMotivoAnulacion() {
		return motivoAnulacion;
	}

	public boolean estaAnulado() {
		return anuladoEn != null;
	}

	public void anular(Usuario anuladoPor, String motivoAnulacion) {
		this.anuladoPor = anuladoPor;
		this.motivoAnulacion = motivoAnulacion;
		this.anuladoEn = Instant.now();
	}
}
