package com.titishop.autenticacion.repository;

import com.titishop.autenticacion.entity.PasswordResetCode;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, UUID> {

	Optional<PasswordResetCode> findTopByUsuarioEmailIgnoreCaseAndUsadoEnIsNullOrderByCreadoEnDesc(String email);
}
