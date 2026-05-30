package com.titishop.productos.repository;

import com.titishop.productos.entity.Categoria;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

	boolean existsByNombreIgnoreCase(String nombre);
}
