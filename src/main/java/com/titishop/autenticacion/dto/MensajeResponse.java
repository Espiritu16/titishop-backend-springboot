package com.titishop.autenticacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MensajeResponse")
public record MensajeResponse(String mensaje) {
}
