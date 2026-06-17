package com.titishop.usuarios.service;

import com.titishop.compartido.response.PaginaResponse;
import com.titishop.usuarios.dto.ActualizarUsuarioRequest;
import com.titishop.usuarios.dto.CrearUsuarioRequest;
import com.titishop.usuarios.dto.UsuarioResponse;
import com.titishop.usuarios.entity.Usuario;
import com.titishop.usuarios.exception.EmailUsuarioDuplicadoException;
import com.titishop.usuarios.exception.UsuarioNoEncontradoException;
import com.titishop.usuarios.repository.UsuarioRepository;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional(readOnly = true)
	public List<UsuarioResponse> listar() {
		return listar(0, Integer.MAX_VALUE, null, null, null).content();
	}

	@Transactional(readOnly = true)
	public PaginaResponse<UsuarioResponse> listar(
			int page,
			int size,
			String busqueda,
			com.titishop.usuarios.entity.RolUsuario rol,
			com.titishop.usuarios.entity.EstadoUsuario estado
	) {
		var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "creadoEn"));
		var pagina = usuarioRepository.findAll(construirFiltro(busqueda, rol, estado), pageable).map(this::toResponse);
		return new PaginaResponse<>(
				pagina.getContent(),
				pagina.getNumber(),
				pagina.getSize(),
				pagina.getTotalElements(),
				pagina.getTotalPages(),
				pagina.isFirst(),
				pagina.isLast(),
				pagina.isEmpty()
		);
	}

	@Transactional(readOnly = true)
	public UsuarioResponse obtenerPorId(UUID id) {
		return toResponse(buscarPorId(id));
	}

	public UsuarioResponse crear(CrearUsuarioRequest request) {
		String email = normalizarEmail(request.email());
		validarEmailNoRegistrado(email);

		Usuario usuario = new Usuario(
				request.nombreCompleto().trim(),
				email,
				passwordEncoder.encode(request.password()),
				request.rol()
		);

		return toResponse(usuarioRepository.save(usuario));
	}

	public UsuarioResponse actualizar(UUID id, ActualizarUsuarioRequest request) {
		Usuario usuario = buscarPorId(id);
		String email = normalizarEmail(request.email());

		if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
			throw new EmailUsuarioDuplicadoException(email);
		}

		usuario.actualizar(
				request.nombreCompleto().trim(),
				email,
				request.rol(),
				request.estado()
		);

		if (request.password() != null && !request.password().isBlank()) {
			usuario.actualizarPassword(passwordEncoder.encode(request.password()));
		}

		return toResponse(usuarioRepository.save(usuario));
	}

	public void inactivar(UUID id) {
		Usuario usuario = buscarPorId(id);
		usuario.inactivar();
		usuarioRepository.save(usuario);
	}

	private Usuario buscarPorId(UUID id) {
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new UsuarioNoEncontradoException(id));
	}

	private void validarEmailNoRegistrado(String email) {
		if (usuarioRepository.existsByEmailIgnoreCase(email)) {
			throw new EmailUsuarioDuplicadoException(email);
		}
	}

	private String normalizarEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}

	private Specification<Usuario> construirFiltro(
			String busqueda,
			com.titishop.usuarios.entity.RolUsuario rol,
			com.titishop.usuarios.entity.EstadoUsuario estado
	) {
		return (root, query, cb) -> {
			var predicates = cb.conjunction();
			String texto = busqueda == null ? "" : busqueda.trim().toLowerCase(Locale.ROOT);
			if (!texto.isEmpty()) {
				String like = "%" + texto + "%";
				predicates = cb.and(predicates, cb.or(
						cb.like(cb.lower(root.get("nombreCompleto")), like),
						cb.like(cb.lower(root.get("email")), like),
						cb.like(cb.lower(root.get("rol").as(String.class)), like)
				));
			}
			if (rol != null) {
				predicates = cb.and(predicates, cb.equal(root.get("rol"), rol));
			}
			if (estado != null) {
				predicates = cb.and(predicates, cb.equal(root.get("estado"), estado));
			}
			return predicates;
		};
	}

	private UsuarioResponse toResponse(Usuario usuario) {
		return new UsuarioResponse(
				usuario.getId(),
				usuario.getNombreCompleto(),
				usuario.getEmail(),
				usuario.getRol(),
				usuario.getEstado(),
				usuario.getCreadoEn(),
				usuario.getActualizadoEn()
		);
	}
}
