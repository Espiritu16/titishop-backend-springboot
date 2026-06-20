package com.titishop.movimientos.repository;

import com.titishop.movimientos.entity.Movimiento;
import com.titishop.productos.entity.Producto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MovimientoRepository extends JpaRepository<Movimiento, UUID>, JpaSpecificationExecutor<Movimiento> {

	List<Movimiento> findByProductoOrderByCreadoEnDesc(Producto producto);
}
