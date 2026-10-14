package com.api.libreria.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.api.libreria.models.ClienteModel;
import com.api.libreria.services.ClienteService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/vista/clientes")
public class ClienteVistaController {

    private final ClienteService clienteService;

    public ClienteVistaController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("cliente", new ClienteModel());
        return mostrar(model);
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        try {
            model.addAttribute("cliente", clienteService.obtenerPorId(id));
            return mostrar(model);
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/vista/clientes";
        }
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("cliente") ClienteModel cliente, BindingResult errores,
            Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            return mostrar(model);
        }
        try {
            if (cliente.getId() == null) {
                clienteService.registrarCliente(cliente);
                flash.addFlashAttribute("mensaje", "Cliente registrado correctamente");
            } else {
                clienteService.actualizarCliente(cliente.getId(), cliente);
                flash.addFlashAttribute("mensaje", "Cliente actualizado correctamente");
            }
            return "redirect:/vista/clientes";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return mostrar(model);
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            clienteService.eliminarCliente(id);
            flash.addFlashAttribute("mensaje", "Cliente eliminado correctamente");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/vista/clientes";
    }

    private String mostrar(Model model) {
        model.addAttribute("clientes", clienteService.listarClientes());
        return "clientes";
    }
}
