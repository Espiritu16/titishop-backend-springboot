package com.titishop.inventario.repository;

import com.titishop.inventario.entity.Inventario;
import com.titishop.productos.entity.Producto;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InventarioRepository extends JpaRepository<Inventario, UUID>, JpaSpecificationExecutor<Inventario> {

	Optional<Inventario> findByProducto(Producto producto);

	Optional<Inventario> findByProductoId(UUID productoId);

	boolean existsByProducto(Producto producto);

	boolean existsByProductoId(UUID productoId);
}
