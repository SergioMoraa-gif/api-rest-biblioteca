package com.api.libreria.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.libreria.exceptions.RecursoNoEncontradoException;
import com.api.libreria.exceptions.ReglaNegocioException;
import com.api.libreria.models.LibroModel;
import com.api.libreria.repositories.ILibroRepository;
import com.api.libreria.repositories.IPrestamoRepository;

@Service
public class LibroServiceImpl implements LibroService {

    private final ILibroRepository libroRepository;
    private final IPrestamoRepository prestamoRepository;

    public LibroServiceImpl(ILibroRepository libroRepository, IPrestamoRepository prestamoRepository) {
        this.libroRepository = libroRepository;
        this.prestamoRepository = prestamoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroModel> listarLibros(String titulo, String autor) {
        boolean hayTitulo = titulo != null && !titulo.isBlank();
        boolean hayAutor = autor != null && !autor.isBlank();

        if (hayTitulo && hayAutor) {
            return libroRepository.findByTituloContainingIgnoreCaseAndAutorContainingIgnoreCase(titulo.trim(), autor.trim());
        }
        if (hayTitulo) {
            return libroRepository.findByTituloContainingIgnoreCase(titulo.trim());
        }
        if (hayAutor) {
            return libroRepository.findByAutorContainingIgnoreCase(autor.trim());
        }
        return libroRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroModel> listarDisponibles() {
        return libroRepository.findByNumEjemplaresGreaterThan(0);
    }

    @Override
    @Transactional(readOnly = true)
    public LibroModel obtenerPorId(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro", id));
    }

    @Override
    @Transactional(readOnly = true)
    public LibroModel obtenerPorIsbn(String isbn) {
        return libroRepository.findByIsbn(isbn.trim())
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro con ISBN " + isbn + " no encontrado"));
    }

    @Override
    @Transactional
    public LibroModel crearLibro(LibroModel libro) {
        libro.setIsbn(libro.getIsbn().trim());
        if (libroRepository.existsByIsbn(libro.getIsbn())) {
            throw new ReglaNegocioException("Ya existe un libro con el ISBN " + libro.getIsbn());
        }
        libro.setId(null); // el id siempre lo genera la base de datos
        return libroRepository.save(libro);
    }

    @Override
    @Transactional
    public LibroModel actualizarLibro(Long id, LibroModel datos) {
        LibroModel libro = obtenerPorId(id);
        String isbn = datos.getIsbn().trim();
        if (libroRepository.existsByIsbnAndIdNot(isbn, id)) {
            throw new ReglaNegocioException("Ya existe otro libro con el ISBN " + isbn);
        }
        libro.setIsbn(isbn);
        libro.setTitulo(datos.getTitulo());
        libro.setAutor(datos.getAutor());
        libro.setEditorial(datos.getEditorial());
        libro.setAnioPublicacion(datos.getAnioPublicacion());
        libro.setNumEjemplares(datos.getNumEjemplares());
        return libroRepository.save(libro);
    }

    @Override
    @Transactional
    public void eliminarLibro(Long id) {
        LibroModel libro = obtenerPorId(id);
        if (prestamoRepository.existsByLibroId(id)) {
            throw new ReglaNegocioException("No se puede eliminar el libro '" + libro.getTitulo()
                    + "' porque tiene préstamos registrados");
        }
        libroRepository.delete(libro);
    }
}
