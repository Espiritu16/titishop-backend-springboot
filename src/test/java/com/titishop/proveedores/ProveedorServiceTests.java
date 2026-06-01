package com.titishop.proveedores;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.titishop.proveedores.dto.ActualizarProveedorRequest;
import com.titishop.proveedores.dto.ConsultaRucProveedorResponse;
import com.titishop.proveedores.dto.CrearProveedorRequest;
import com.titishop.proveedores.dto.EstadoProveedor;
import com.titishop.proveedores.entity.Proveedor;
import com.titishop.proveedores.exception.EmailProveedorDuplicadoException;
import com.titishop.proveedores.exception.ProveedorNoEncontradoException;
import com.titishop.proveedores.exception.RucProveedorDuplicadoException;
import com.titishop.proveedores.repository.ProveedorRepository;
import com.titishop.proveedores.service.FactilizaProveedorClient;
import com.titishop.proveedores.service.ProveedorService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProveedorServiceTests {

	@Mock
	private ProveedorRepository proveedorRepository;

	@Mock
	private FactilizaProveedorClient factilizaProveedorClient;

	private ProveedorService proveedorService;

	@BeforeEach
	void setUp() {
		proveedorService = new ProveedorService(proveedorRepository, factilizaProveedorClient);
	}

	@Test
	void crearFallaSiRucDuplicado() {
		CrearProveedorRequest request = crearRequest();
		when(proveedorRepository.existsByRuc("20609998881")).thenReturn(true);

		assertThatThrownBy(() -> proveedorService.crear(request))
				.isInstanceOf(RucProveedorDuplicadoException.class);
		verify(proveedorRepository, never()).save(any(Proveedor.class));
	}

	@Test
	void actualizarFallaSiEmailDuplicado() {
		UUID id = UUID.randomUUID();
		Proveedor proveedor = new Proveedor(
				"Proveedor Uno",
				"20609998881",
				"987654321",
				"014700000",
				"ventas@uno.pe",
				"Av. Uno 123"
		);
		ActualizarProveedorRequest request = new ActualizarProveedorRequest(
				"Proveedor Dos",
				"20609998881",
				"987654321",
				"014700000",
				"ventas@dos.pe",
				"Av. Dos 123",
				EstadoProveedor.ACTIVO
		);

		when(proveedorRepository.findById(id)).thenReturn(Optional.of(proveedor));
		when(proveedorRepository.existsByEmailIgnoreCaseAndIdNot("ventas@dos.pe", id)).thenReturn(true);

		assertThatThrownBy(() -> proveedorService.actualizar(id, request))
				.isInstanceOf(EmailProveedorDuplicadoException.class);
	}

	@Test
	void inactivarMarcaProveedorComoInactivo() {
		UUID id = UUID.randomUUID();
		Proveedor proveedor = new Proveedor(
				"Proveedor Uno",
				"20609998881",
				"987654321",
				"014700000",
				"ventas@uno.pe",
				"Av. Uno 123"
		);
		when(proveedorRepository.findById(id)).thenReturn(Optional.of(proveedor));

		proveedorService.inactivar(id);

		assertThat(proveedor.getEstado().name()).isEqualTo("INACTIVO");
		verify(proveedorRepository).save(proveedor);
	}

	@Test
	void consultarRucFallaSiProveedorYaExiste() {
		when(proveedorRepository.existsByRuc("20609998881")).thenReturn(true);

		assertThatThrownBy(() -> proveedorService.consultarRuc("20609998881"))
				.isInstanceOf(RucProveedorDuplicadoException.class);
		verify(factilizaProveedorClient, never()).consultarRuc("20609998881");
	}

	@Test
	void consultarRucRetornaDatosFactiliza() {
		ConsultaRucProveedorResponse response = new ConsultaRucProveedorResponse(
				"20609998881",
				"FACTILIZA S.A.C.",
				"AV. LOS OLIVOS 123",
				"AV. LOS OLIVOS 123 LIMA LIMA MIRAFLORES",
				"LIMA",
				"LIMA",
				"MIRAFLORES",
				"ACTIVO",
				"HABIDO"
		);
		when(proveedorRepository.existsByRuc("20609998881")).thenReturn(false);
		when(factilizaProveedorClient.consultarRuc("20609998881")).thenReturn(response);

		ConsultaRucProveedorResponse result = proveedorService.consultarRuc("20609998881");

		assertThat(result.razonSocial()).isEqualTo("FACTILIZA S.A.C.");
		assertThat(result.direccion()).isEqualTo("AV. LOS OLIVOS 123");
	}

	@Test
	void obtenerPorIdFallaSiNoExiste() {
		UUID id = UUID.randomUUID();
		when(proveedorRepository.findById(id)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> proveedorService.obtenerPorId(id))
				.isInstanceOf(ProveedorNoEncontradoException.class);
	}

	private CrearProveedorRequest crearRequest() {
		return new CrearProveedorRequest(
				"Proveedor Uno",
				"20609998881",
				"987654321",
				"014700000",
				"ventas@uno.pe",
				"Av. Uno 123"
		);
	}
}
