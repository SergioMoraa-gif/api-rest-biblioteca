package com.api.libreria.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.libreria.models.LibroModel;

@Repository
public interface ILibroRepository extends JpaRepository<LibroModel, Long> {

    Optional<LibroModel> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    List<LibroModel> findByTituloContainingIgnoreCase(String titulo);

    List<LibroModel> findByAutorContainingIgnoreCase(String autor);

    List<LibroModel> findByTituloContainingIgnoreCaseAndAutorContainingIgnoreCase(String titulo, String autor);

    List<LibroModel> findByNumEjemplaresGreaterThan(int cantidad);
}
