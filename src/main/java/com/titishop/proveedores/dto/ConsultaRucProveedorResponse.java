package com.titishop.proveedores.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ConsultaRucProveedorResponse", description = "Respuesta obtenida al consultar un RUC en el servicio externo.")
public record ConsultaRucProveedorResponse(
		@Schema(description = "RUC consultado.", example = "20123456789")
		String ruc,
		@Schema(description = "Razon social registrada en SUNAT.", example = "Distribuidora Lima Norte SAC")
		String razonSocial,
		@Schema(description = "Direccion resumida.", example = "Av. Los Almacenes 321")
		String direccion,
		@Schema(description = "Direccion completa.", example = "Av. Los Almacenes 321, Independencia, Lima")
		String direccionCompleta,
		@Schema(description = "Departamento.", example = "LIMA")
		String departamento,
		@Schema(description = "Provincia.", example = "LIMA")
		String provincia,
		@Schema(description = "Distrito.", example = "INDEPENDENCIA")
		String distrito,
		@Schema(description = "Estado tributario.", example = "ACTIVO")
		String estadoContribuyente,
		@Schema(description = "Condicion tributaria.", example = "HABIDO")
		String condicionContribuyente
) {
}
