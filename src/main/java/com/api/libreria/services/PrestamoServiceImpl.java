package com.api.libreria.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.libreria.dto.PrestamoRequest;
import com.api.libreria.exceptions.RecursoNoEncontradoException;
import com.api.libreria.exceptions.ReglaNegocioException;
import com.api.libreria.models.ClienteModel;
import com.api.libreria.models.EstadoPrestamo;
import com.api.libreria.models.LibroModel;
import com.api.libreria.models.PrestamoModel;
import com.api.libreria.repositories.ILibroRepository;
import com.api.libreria.repositories.IPrestamoRepository;

@Service
public class PrestamoServiceImpl implements PrestamoService {

    private static final int DIAS_POR_DEFECTO = 14;

    private final IPrestamoRepository prestamoRepository;
    private final ILibroRepository libroRepository;
    private final ClienteService clienteService;
    private final LibroService libroService;

    public PrestamoServiceImpl(IPrestamoRepository prestamoRepository, ILibroRepository libroRepository,
            ClienteService clienteService, LibroService libroService) {
        this.prestamoRepository = prestamoRepository;
        this.libroRepository = libroRepository;
        this.clienteService = clienteService;
        this.libroService = libroService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoModel> listarPrestamos(EstadoPrestamo estado) {
        if (estado == null) {
            return prestamoRepository.findAllByOrderByIdDesc();
        }
        return prestamoRepository.findByEstadoOrderByFechaPrestamoDesc(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoModel> listarPorCliente(Long clienteId) {
        clienteService.obtenerPorId(clienteId); // 404 si el cliente no existe
        return prestamoRepository.findByClienteIdOrderByIdDesc(clienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public PrestamoModel obtenerPorId(Long id) {
        return prestamoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo", id));
    }

    /**
     * Todo ocurre en una sola transacción: si algo falla, no se descuenta el
     * ejemplar ni se guarda el préstamo.
     */
    @Override
    @Transactional
    public PrestamoModel prestar(PrestamoRequest solicitud) {
        ClienteModel cliente = clienteService.obtenerPorId(solicitud.getClienteId());
        LibroModel libro = libroService.obtenerPorId(solicitud.getLibroId());

        if (libro.getNumEjemplares() <= 0) {
            throw new ReglaNegocioException("No hay ejemplares disponibles de '" + libro.getTitulo() + "'");
        }
        if (prestamoRepository.existsByClienteIdAndLibroIdAndEstado(cliente.getId(), libro.getId(), EstadoPrestamo.ACTIVO)) {
            throw new ReglaNegocioException(cliente.getNombreCompleto() + " ya tiene un préstamo activo de '"
                    + libro.getTitulo() + "'");
        }

        libro.setNumEjemplares(libro.getNumEjemplares() - 1);
        libroRepository.save(libro);

        int dias = solicitud.getDias() != null ? solicitud.getDias() : DIAS_POR_DEFECTO;
        LocalDate hoy = LocalDate.now();

        PrestamoModel prestamo = new PrestamoModel();
        prestamo.setCliente(cliente);
        prestamo.setLibro(libro);
        prestamo.setFechaPrestamo(hoy);
        prestamo.setFechaLimite(hoy.plusDays(dias));
        prestamo.setEstado(EstadoPrestamo.ACTIVO);
        return prestamoRepository.save(prestamo);
    }

    @Override
    @Transactional
    public PrestamoModel devolver(Long id) {
        PrestamoModel prestamo = obtenerPorId(id);
        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new ReglaNegocioException("El préstamo " + id + " ya fue devuelto el " + prestamo.getFechaDevolucion());
        }

        LibroModel libro = prestamo.getLibro();
        libro.setNumEjemplares(libro.getNumEjemplares() + 1);
        libroRepository.save(libro);

        prestamo.setFechaDevolucion(LocalDate.now());
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        return prestamoRepository.save(prestamo);
    }

    @Override
    @Transactional
    public void eliminarPrestamo(Long id) {
        PrestamoModel prestamo = obtenerPorId(id);
        if (prestamo.getEstado() == EstadoPrestamo.ACTIVO) {
            throw new ReglaNegocioException("No se puede eliminar un préstamo activo; primero registra la devolución");
        }
        prestamoRepository.delete(prestamo);
    }
}
