package com.api.libreria.dto;

import java.time.LocalDateTime;
import java.util.Map;

/** Formato único para todas las respuestas de error de la API. */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String ruta,
        Map<String, String> detalles) {
}
