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

import com.api.libreria.models.LibroModel;
import com.api.libreria.services.LibroService;

import jakarta.validation.Valid;

/** Vista MVC (Thymeleaf) del catálogo. Usa el mismo LibroService que la API REST. */
@Controller
@RequestMapping("/vista/libros")
public class LibroVistaController {

    private final LibroService libroService;

    public LibroVistaController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("libro", new LibroModel());
        return mostrar(model);
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        try {
            model.addAttribute("libro", libroService.obtenerPorId(id));
            return mostrar(model);
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/vista/libros";
        }
    }

    // Si el formulario trae id, actualiza; si no, crea.
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("libro") LibroModel libro, BindingResult errores,
            Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            return mostrar(model);
        }
        try {
            if (libro.getId() == null) {
                libroService.crearLibro(libro);
                flash.addFlashAttribute("mensaje", "Libro registrado correctamente");
            } else {
                libroService.actualizarLibro(libro.getId(), libro);
                flash.addFlashAttribute("mensaje", "Libro actualizado correctamente");
            }
            return "redirect:/vista/libros";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return mostrar(model);
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            libroService.eliminarLibro(id);
            flash.addFlashAttribute("mensaje", "Libro eliminado correctamente");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/vista/libros";
    }

    private String mostrar(Model model) {
        model.addAttribute("libros", libroService.listarLibros(null, null));
        return "libros";
    }
}
