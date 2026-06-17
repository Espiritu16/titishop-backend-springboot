package com.titishop.productos.repository;

import com.titishop.productos.entity.Marca;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MarcaRepository extends JpaRepository<Marca, UUID>, JpaSpecificationExecutor<Marca> {

	boolean existsByNombreIgnoreCase(String nombre);

	boolean existsByNombreIgnoreCaseAndIdNot(String nombre, UUID id);
}
