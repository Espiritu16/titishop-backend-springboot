package com.titishop.productos.repository;

import com.titishop.productos.entity.Producto;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, UUID> {

	boolean existsBySkuIgnoreCase(String sku);
}
