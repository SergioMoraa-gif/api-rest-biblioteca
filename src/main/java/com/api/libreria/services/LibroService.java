package com.api.libreria.services;

import java.util.List;

import com.api.libreria.models.LibroModel;

/** Servicio de entidad: operaciones sobre el catálogo de libros. */
public interface LibroService {

    List<LibroModel> listarLibros(String titulo, String autor);

    List<LibroModel> listarDisponibles();

    LibroModel obtenerPorId(Long id);

    LibroModel obtenerPorIsbn(String isbn);

    LibroModel crearLibro(LibroModel libro);

    LibroModel actualizarLibro(Long id, LibroModel datos);

    void eliminarLibro(Long id);
}
