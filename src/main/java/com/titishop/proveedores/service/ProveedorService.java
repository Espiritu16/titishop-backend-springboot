package com.titishop.proveedores.service;

import com.titishop.compartido.response.PaginaResponse;
import com.titishop.proveedores.dto.ActualizarProveedorRequest;
import com.titishop.proveedores.dto.ConsultaRucProveedorResponse;
import com.titishop.proveedores.dto.CrearProveedorRequest;
import com.titishop.proveedores.dto.EstadoProveedor;
import com.titishop.proveedores.dto.ProveedorResponse;
import com.titishop.proveedores.entity.Proveedor;
import com.titishop.proveedores.exception.EmailProveedorDuplicadoException;
import com.titishop.proveedores.exception.ProveedorNoEncontradoException;
import com.titishop.proveedores.exception.RucProveedorDuplicadoException;
import com.titishop.proveedores.repository.ProveedorRepository;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProveedorService {

	private final ProveedorRepository proveedorRepository;

	private final FactilizaProveedorClient factilizaProveedorClient;

	public ProveedorService(ProveedorRepository proveedorRepository, FactilizaProveedorClient factilizaProveedorClient) {
		this.proveedorRepository = proveedorRepository;
		this.factilizaProveedorClient = factilizaProveedorClient;
	}

	@Transactional(readOnly = true)
	public List<ProveedorResponse> listar() {
		return listar(0, Integer.MAX_VALUE, null, null).content();
	}

	@Transactional(readOnly = true)
	public PaginaResponse<ProveedorResponse> listar(int page, int size, String busqueda, EstadoProveedor estado) {
		var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "creadoEn"));
		var pagina = proveedorRepository.findAll(construirFiltro(busqueda, estado), pageable).map(this::toResponse);
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
	public ProveedorResponse obtenerPorId(UUID id) {
		return toResponse(buscarPorId(id));
	}

	@Transactional(readOnly = true)
	public ConsultaRucProveedorResponse consultarRuc(String ruc) {
		String rucNormalizado = normalizarSoloDigitos(ruc);
		if (proveedorRepository.existsByRuc(rucNormalizado)) {
			throw new RucProveedorDuplicadoException(rucNormalizado);
		}
		return factilizaProveedorClient.consultarRuc(rucNormalizado);
	}

	public ProveedorResponse crear(CrearProveedorRequest request) {
		String ruc = normalizarSoloDigitos(request.ruc());
		String email = normalizarEmail(request.email());
		validarRucNoRegistrado(ruc);
		validarEmailNoRegistrado(email);

		Proveedor proveedor = new Proveedor(
				request.razonSocial().trim(),
				ruc,
				normalizarContactoNumerico(request.celular()),
				normalizarContactoNumerico(request.telefono()),
				email,
				request.direccion().trim()
		);

		return toResponse(proveedorRepository.save(proveedor));
	}

	public ProveedorResponse actualizar(UUID id, ActualizarProveedorRequest request) {
		Proveedor proveedor = buscarPorId(id);
		String ruc = normalizarSoloDigitos(request.ruc());
		String email = normalizarEmail(request.email());

		if (proveedorRepository.existsByRucAndIdNot(ruc, id)) {
			throw new RucProveedorDuplicadoException(ruc);
		}
		if (email != null && proveedorRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
			throw new EmailProveedorDuplicadoException(email);
		}

		proveedor.actualizar(
				request.razonSocial().trim(),
				ruc,
				normalizarContactoNumerico(request.celular()),
				normalizarContactoNumerico(request.telefono()),
				email,
				request.direccion().trim(),
				com.titishop.proveedores.entity.EstadoProveedor.valueOf(request.estado().name())
		);

		return toResponse(proveedorRepository.save(proveedor));
	}

	public void inactivar(UUID id) {
		Proveedor proveedor = buscarPorId(id);
		proveedor.inactivar();
		proveedorRepository.save(proveedor);
	}

	private Proveedor buscarPorId(UUID id) {
		return proveedorRepository.findById(id)
				.orElseThrow(() -> new ProveedorNoEncontradoException(id));
	}

	private void validarRucNoRegistrado(String ruc) {
		if (proveedorRepository.existsByRuc(ruc)) {
			throw new RucProveedorDuplicadoException(ruc);
		}
	}

	private void validarEmailNoRegistrado(String email) {
		if (email == null) {
			return;
		}
		if (proveedorRepository.existsByEmailIgnoreCase(email)) {
			throw new EmailProveedorDuplicadoException(email);
		}
	}

	private String normalizarSoloDigitos(String value) {
		return value == null ? "" : value.replaceAll("\\D", "");
	}

	private String normalizarContactoNumerico(String value) {
		String digits = normalizarSoloDigitos(value);
		return digits.isBlank() ? null : digits;
	}

	private String normalizarEmail(String email) {
		if (email == null || email.isBlank()) {
			return null;
		}
		return email.trim().toLowerCase(Locale.ROOT);
	}

	private Specification<Proveedor> construirFiltro(String busqueda, EstadoProveedor estado) {
		return (root, query, cb) -> {
			var predicates = cb.conjunction();
			String texto = busqueda == null ? "" : busqueda.trim().toLowerCase(Locale.ROOT);
			if (!texto.isEmpty()) {
				String like = "%" + texto + "%";
				predicates = cb.and(predicates, cb.or(
						cb.like(cb.lower(root.get("razonSocial")), like),
						cb.like(cb.lower(root.get("ruc")), like),
						cb.like(cb.lower(root.get("email")), like),
						cb.like(cb.lower(root.get("direccion")), like)
				));
			}
			if (estado != null) {
				predicates = cb.and(
						predicates,
						cb.equal(root.get("estado"), com.titishop.proveedores.entity.EstadoProveedor.valueOf(estado.name()))
				);
			}
			return predicates;
		};
	}

	private ProveedorResponse toResponse(Proveedor proveedor) {
		return new ProveedorResponse(
				proveedor.getId(),
				proveedor.getRazonSocial(),
				proveedor.getRuc(),
				proveedor.getCelular(),
				proveedor.getTelefono(),
				proveedor.getEmail(),
				proveedor.getDireccion(),
				com.titishop.proveedores.dto.EstadoProveedor.valueOf(proveedor.getEstado().name()),
				proveedor.getCreadoEn(),
				proveedor.getActualizadoEn()
		);
	}
}
