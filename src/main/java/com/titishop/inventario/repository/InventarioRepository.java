package com.titishop.inventario.repository;

import com.titishop.inventario.entity.Inventario;
import com.titishop.productos.entity.Producto;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioRepository extends JpaRepository<Inventario, UUID> {

	Optional<Inventario> findByProducto(Producto producto);

	boolean existsByProducto(Producto producto);
}
