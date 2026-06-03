package com.titishop.proveedores.service;

import com.titishop.proveedores.dto.ConsultaRucProveedorResponse;
import com.titishop.proveedores.exception.FactilizaDocumentoNoEncontradoException;
import com.titishop.proveedores.exception.FactilizaServicioNoDisponibleException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class FactilizaProveedorRestClient implements FactilizaProveedorClient {

	private final FactilizaProperties properties;

	private final RestClient restClient;

	public FactilizaProveedorRestClient(FactilizaProperties properties) {
		this.properties = properties;
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(properties.getConnectTimeout());
		requestFactory.setReadTimeout(properties.getReadTimeout());
		this.restClient = RestClient.builder()
				.baseUrl(properties.getApiBaseUrl().replaceAll("/+$", ""))
				.requestFactory(requestFactory)
				.build();
	}

	@Override
	public ConsultaRucProveedorResponse consultarRuc(String ruc) {
		String token = properties.getApiToken() == null ? "" : properties.getApiToken().trim();
		if (token.isEmpty()) {
			throw new FactilizaServicioNoDisponibleException();
		}

		FactilizaRucResponse response;
		try {
			response = restClient.get()
					.uri("/ruc/info/{ruc}", ruc)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
					.retrieve()
					.onStatus(status -> status == HttpStatus.NOT_FOUND || status.value() == 422,
							(request, clientResponse) -> {
								throw new FactilizaDocumentoNoEncontradoException();
							})
					.onStatus(status -> status.is5xxServerError(),
							(request, clientResponse) -> {
								throw new FactilizaServicioNoDisponibleException();
							})
					.onStatus(status -> status.isError(),
							(request, clientResponse) -> {
								throw new FactilizaDocumentoNoEncontradoException();
							})
					.body(FactilizaRucResponse.class);
		} catch (FactilizaDocumentoNoEncontradoException | FactilizaServicioNoDisponibleException ex) {
			throw ex;
		} catch (RestClientException ex) {
			throw new FactilizaServicioNoDisponibleException(ex);
		}

		if (response == null || Boolean.FALSE.equals(response.success()) || response.data() == null || response.data().numero() == null) {
			throw new FactilizaDocumentoNoEncontradoException();
		}

		FactilizaRucData data = response.data();
		return new ConsultaRucProveedorResponse(
				data.numero(),
				data.nombre_o_razon_social(),
				data.direccion(),
				data.direccion_completa(),
				data.departamento(),
				data.provincia(),
				data.distrito(),
				data.estado(),
				data.condicion()
		);
	}

	private record FactilizaRucResponse(
			Integer status,
			Boolean success,
			String message,
			FactilizaRucData data
	) {
	}

	private record FactilizaRucData(
			String numero,
			String nombre_o_razon_social,
			String tipo_contribuyente,
			String estado,
			String condicion,
			String departamento,
			String provincia,
			String distrito,
			String direccion,
			String direccion_completa
	) {
	}
}
