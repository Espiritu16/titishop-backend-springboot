package com.titishop.proveedores.dto;

public record ConsultaRucProveedorResponse(
		String ruc,
		String razonSocial,
		String direccion,
		String direccionCompleta,
		String departamento,
		String provincia,
		String distrito,
		String estadoContribuyente,
		String condicionContribuyente
) {
}
