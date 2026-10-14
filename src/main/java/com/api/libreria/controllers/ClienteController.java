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

import com.api.libreria.models.ClienteModel;
import com.api.libreria.models.PrestamoModel;
import com.api.libreria.services.ClienteService;
import com.api.libreria.services.PrestamoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final PrestamoService prestamoService;

    public ClienteController(ClienteService clienteService, PrestamoService prestamoService) {
        this.clienteService = clienteService;
        this.prestamoService = prestamoService;
    }

    @GetMapping
    public ResponseEntity<List<ClienteModel>> listar() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteModel> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }

    // GET /api/clientes/buscar?correo=ana@correo.com
    @GetMapping("/buscar")
    public ResponseEntity<ClienteModel> obtenerPorCorreo(@RequestParam String correo) {
        return ResponseEntity.ok(clienteService.obtenerPorCorreo(correo));
    }

    // Historial de préstamos de un cliente
    @GetMapping("/{id}/prestamos")
    public ResponseEntity<List<PrestamoModel>> prestamosDelCliente(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.listarPorCliente(id));
    }

    @PostMapping
    public ResponseEntity<ClienteModel> crear(@Valid @RequestBody ClienteModel cliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.registrarCliente(cliente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteModel> actualizar(@PathVariable Long id, @Valid @RequestBody ClienteModel cliente) {
        return ResponseEntity.ok(clienteService.actualizarCliente(id, cliente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
