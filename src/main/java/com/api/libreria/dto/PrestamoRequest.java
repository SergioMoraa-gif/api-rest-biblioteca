package com.api.libreria.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Datos que el consumidor envía para registrar un préstamo.
 * Solo se piden los ids: el servicio busca el cliente y el libro y calcula las fechas.
 */
public class PrestamoRequest {

    @NotNull(message = "El id del cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "El id del libro es obligatorio")
    private Long libroId;

    // Opcional. Si no se envía se prestan 14 días.
    @Min(value = 1, message = "El préstamo debe ser de al menos 1 día")
    @Max(value = 30, message = "El préstamo no puede superar 30 días")
    private Integer dias;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getLibroId() { return libroId; }
    public void setLibroId(Long libroId) { this.libroId = libroId; }

    public Integer getDias() { return dias; }
    public void setDias(Integer dias) { this.dias = dias; }
}
