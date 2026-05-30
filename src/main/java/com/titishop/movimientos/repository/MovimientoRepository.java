package com.titishop.movimientos.repository;

import com.titishop.movimientos.entity.Movimiento;
import com.titishop.productos.entity.Producto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepository extends JpaRepository<Movimiento, UUID> {

	List<Movimiento> findByProductoOrderByCreadoEnDesc(Producto producto);
}
