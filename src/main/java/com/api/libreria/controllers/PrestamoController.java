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

import com.api.libreria.dto.PrestamoRequest;
import com.api.libreria.models.EstadoPrestamo;
import com.api.libreria.models.PrestamoModel;
import com.api.libreria.services.PrestamoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    // GET /api/prestamos  |  /api/prestamos?estado=ACTIVO  |  /api/prestamos?estado=DEVUELTO
    @GetMapping
    public ResponseEntity<List<PrestamoModel>> listar(@RequestParam(required = false) EstadoPrestamo estado) {
        return ResponseEntity.ok(prestamoService.listarPrestamos(estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestamoModel> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.obtenerPorId(id));
    }

    // Prestar: { "clienteId": 1, "libroId": 2, "dias": 7 }
    @PostMapping
    public ResponseEntity<PrestamoModel> prestar(@Valid @RequestBody PrestamoRequest solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prestamoService.prestar(solicitud));
    }

    // Devolver: registra la fecha de devolución y regresa el ejemplar al catálogo
    @PutMapping("/{id}/devolucion")
    public ResponseEntity<PrestamoModel> devolver(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.devolver(id));
    }

    // Solo se pueden eliminar préstamos ya devueltos
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        prestamoService.eliminarPrestamo(id);
        return ResponseEntity.noContent().build();
    }
}
