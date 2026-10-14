package com.api.libreria.services;

import java.util.List;

import com.api.libreria.dto.PrestamoRequest;
import com.api.libreria.models.EstadoPrestamo;
import com.api.libreria.models.PrestamoModel;

/**
 * Servicio de tarea: coordina a Cliente y Libro para prestar y devolver.
 * Es el equivalente a "Inscribir estudiante" del caso conductor (orquestación).
 */
public interface PrestamoService {

    List<PrestamoModel> listarPrestamos(EstadoPrestamo estado);

    List<PrestamoModel> listarPorCliente(Long clienteId);

    PrestamoModel obtenerPorId(Long id);

    PrestamoModel prestar(PrestamoRequest solicitud);

    PrestamoModel devolver(Long id);

    void eliminarPrestamo(Long id);
}
