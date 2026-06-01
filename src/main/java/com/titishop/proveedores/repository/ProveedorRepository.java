package com.titishop.proveedores.repository;

import com.titishop.proveedores.entity.Proveedor;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, UUID> {

	boolean existsByRuc(String ruc);

	boolean existsByRucAndIdNot(String ruc, UUID id);

	boolean existsByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);
}
