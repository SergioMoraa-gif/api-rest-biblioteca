package com.api.libreria.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.api.libreria.dto.PrestamoRequest;
import com.api.libreria.models.EstadoPrestamo;
import com.api.libreria.services.ClienteService;
import com.api.libreria.services.LibroService;
import com.api.libreria.services.PrestamoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/vista/prestamos")
public class PrestamoVistaController {

    private final PrestamoService prestamoService;
    private final ClienteService clienteService;
    private final LibroService libroService;

    public PrestamoVistaController(PrestamoService prestamoService, ClienteService clienteService,
            LibroService libroService) {
        this.prestamoService = prestamoService;
        this.clienteService = clienteService;
        this.libroService = libroService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) EstadoPrestamo estado, Model model) {
        PrestamoRequest solicitud = new PrestamoRequest();
        solicitud.setDias(14);
        model.addAttribute("solicitud", solicitud);
        return mostrar(estado, model);
    }

    @PostMapping("/prestar")
    public String prestar(@Valid @ModelAttribute("solicitud") PrestamoRequest solicitud, BindingResult errores,
            Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            return mostrar(null, model);
        }
        try {
            prestamoService.prestar(solicitud);
            flash.addFlashAttribute("mensaje", "Préstamo registrado correctamente");
            return "redirect:/vista/prestamos";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return mostrar(null, model);
        }
    }

    @PostMapping("/devolver/{id}")
    public String devolver(@PathVariable Long id, RedirectAttributes flash) {
        try {
            prestamoService.devolver(id);
            flash.addFlashAttribute("mensaje", "Devolución registrada correctamente");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/vista/prestamos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            prestamoService.eliminarPrestamo(id);
            flash.addFlashAttribute("mensaje", "Préstamo eliminado del historial");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/vista/prestamos";
    }

    private String mostrar(EstadoPrestamo estado, Model model) {
        model.addAttribute("prestamos", prestamoService.listarPrestamos(estado));
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("librosDisponibles", libroService.listarDisponibles());
        model.addAttribute("estadoFiltro", estado);
        return "prestamos";
    }
}
