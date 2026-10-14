package com.api.libreria.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.libreria.models.LibroModel;
import com.api.libreria.services.LibroService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    // GET /api/libros  |  /api/libros?titulo=quijote  |  /api/libros?autor=cervantes
    @GetMapping
    public ResponseEntity<List<LibroModel>> listar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor) {
        return ResponseEntity.ok(libroService.listarLibros(titulo, autor));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<LibroModel>> disponibles() {
        return ResponseEntity.ok(libroService.listarDisponibles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibroModel> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(libroService.obtenerPorId(id));
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<LibroModel> obtenerPorIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(libroService.obtenerPorIsbn(isbn));
    }

    @PostMapping
    public ResponseEntity<LibroModel> crear(@Valid @RequestBody LibroModel libro) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libroService.crearLibro(libro));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LibroModel> actualizar(@PathVariable Long id, @Valid @RequestBody LibroModel libro) {
        return ResponseEntity.ok(libroService.actualizarLibro(id, libro));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        libroService.eliminarLibro(id);
        return ResponseEntity.noContent().build();
    }
}
