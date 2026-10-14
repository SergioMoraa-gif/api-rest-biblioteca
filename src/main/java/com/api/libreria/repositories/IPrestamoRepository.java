package com.api.libreria.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.libreria.models.EstadoPrestamo;
import com.api.libreria.models.PrestamoModel;

@Repository
public interface IPrestamoRepository extends JpaRepository<PrestamoModel, Long> {

    List<PrestamoModel> findByEstadoOrderByFechaPrestamoDesc(EstadoPrestamo estado);

    List<PrestamoModel> findAllByOrderByIdDesc();

    List<PrestamoModel> findByClienteIdOrderByIdDesc(Long clienteId);

    boolean existsByLibroId(Long libroId);

    boolean existsByClienteId(Long clienteId);

    boolean existsByClienteIdAndLibroIdAndEstado(Long clienteId, Long libroId, EstadoPrestamo estado);
}
