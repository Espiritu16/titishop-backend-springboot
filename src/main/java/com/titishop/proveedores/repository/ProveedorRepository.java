package com.titishop.proveedores.repository;

import com.titishop.proveedores.entity.Proveedor;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, UUID> {

	boolean existsByRuc(String ruc);

	boolean existsByEmailIgnoreCase(String email);
}
