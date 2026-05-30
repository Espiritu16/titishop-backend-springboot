package com.titishop.productos.entity;

import com.titishop.compartido.entity.AuditoriaEntity;
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

	@Column(name = "precio_compra", nullable = false, precision = 12, scale = 2)
	private BigDecimal precioCompra = BigDecimal.ZERO;

	@Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
	private BigDecimal precioVenta = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoProducto estado = EstadoProducto.ACTIVO;
}
