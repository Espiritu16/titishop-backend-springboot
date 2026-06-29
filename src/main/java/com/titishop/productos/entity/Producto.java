package com.titishop.productos.entity;

import com.titishop.compartido.entity.AuditoriaEntity;
import com.titishop.proveedores.entity.Proveedor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "productos")
public class Producto extends AuditoriaEntity {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(nullable = false, length = 120)
	private String nombre;

	@Column(nullable = false, unique = true, length = 40)
	private String sku;

	@Lob
	private String descripcion;

	@Column(name = "imagen_url", length = 500)
	private String imagenUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoria_id", nullable = false)
	private Categoria categoria;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "marca_id", nullable = false)
	private Marca marca;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "proveedor_id", nullable = false)
	private Proveedor proveedor;

	@Column(name = "pais_origen", nullable = false, length = 80)
	private String paisOrigen;

	@Column(name = "precio_compra", nullable = false, precision = 12, scale = 2)
	private BigDecimal precioCompra = BigDecimal.ZERO;

	@Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
	private BigDecimal precioVenta = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoProducto estado = EstadoProducto.ACTIVO;

	protected Producto() {
	}

	public Producto(
			String nombre,
			String sku,
			String descripcion,
			String imagenUrl,
			Categoria categoria,
			Marca marca,
			BigDecimal precioCompra,
			BigDecimal precioVenta
	) {
		this(nombre, sku, descripcion, imagenUrl, categoria, marca, null, "No especificado", precioCompra, precioVenta);
	}

	public Producto(
			String nombre,
			String sku,
			String descripcion,
			String imagenUrl,
			Categoria categoria,
			Marca marca,
			Proveedor proveedor,
			String paisOrigen,
			BigDecimal precioCompra,
			BigDecimal precioVenta
	) {
		this.nombre = nombre;
		this.sku = sku;
		this.descripcion = descripcion;
		this.imagenUrl = imagenUrl;
		this.categoria = categoria;
		this.marca = marca;
		this.proveedor = proveedor;
		this.paisOrigen = paisOrigen;
		this.precioCompra = precioCompra;
		this.precioVenta = precioVenta;
		this.estado = EstadoProducto.ACTIVO;
	}

	public UUID getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public String getSku() {
		return sku;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getImagenUrl() {
		return imagenUrl;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public Marca getMarca() {
		return marca;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public String getPaisOrigen() {
		return paisOrigen;
	}

	public BigDecimal getPrecioCompra() {
		return precioCompra;
	}

	public BigDecimal getPrecioVenta() {
		return precioVenta;
	}

	public EstadoProducto getEstado() {
		return estado;
	}

	public void actualizar(
			String nombre,
			String sku,
			String descripcion,
			String imagenUrl,
			Categoria categoria,
			Marca marca,
			Proveedor proveedor,
			String paisOrigen,
			BigDecimal precioCompra,
			BigDecimal precioVenta,
			EstadoProducto estado
	) {
		this.nombre = nombre;
		this.sku = sku;
		this.descripcion = descripcion;
		this.imagenUrl = imagenUrl;
		this.categoria = categoria;
		this.marca = marca;
		this.proveedor = proveedor;
		this.paisOrigen = paisOrigen;
		this.precioCompra = precioCompra;
		this.precioVenta = precioVenta;
		this.estado = estado;
		setActualizadoEn(Instant.now());
	}

	public void inactivar() {
		this.estado = EstadoProducto.INACTIVO;
		setInactivadoEn(Instant.now());
		setActualizadoEn(Instant.now());
	}
}
