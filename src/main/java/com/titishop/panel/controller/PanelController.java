package com.titishop.panel.controller;

import com.titishop.panel.dto.PanelResumenResponse;
import com.titishop.panel.service.PanelService;
import com.titishop.compartido.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/panel")
@Tag(name = "Panel", description = "Resumen ejecutivo del sistema.")
@SecurityRequirement(name = "bearerAuth")
public class PanelController {

	private final PanelService panelService;

	public PanelController(PanelService panelService) {
		this.panelService = panelService;
	}

	@GetMapping("/resumen")
	@Operation(summary = "Obtener resumen del panel", description = "Devuelve metricas globales y ultimos movimientos para el dashboard.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Resumen obtenido correctamente.",
					content = @Content(schema = @Schema(implementation = PanelResumenResponse.class))),
			@ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para el rol autenticado."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public PanelResumenResponse resumen() {
		return panelService.resumen();
	}
}
