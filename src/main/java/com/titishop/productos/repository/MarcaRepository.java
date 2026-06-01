package com.titishop.productos.repository;

import com.titishop.productos.entity.Marca;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarcaRepository extends JpaRepository<Marca, UUID> {

	boolean existsByNombreIgnoreCase(String nombre);

	boolean existsByNombreIgnoreCaseAndIdNot(String nombre, UUID id);
}
