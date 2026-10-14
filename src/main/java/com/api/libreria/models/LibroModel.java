package com.api.libreria.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "libros")
public class LibroModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ISBN real (10 o 13 dígitos, puede llevar guiones). Lo captura el usuario.
    @NotBlank(message = "El ISBN es obligatorio")
    @Pattern(regexp = "^[0-9-]{10,17}$", message = "El ISBN solo admite dígitos y guiones (10 a 17 caracteres)")
    @Column(nullable = false, unique = true, length = 17)
    private String isbn;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título admite máximo 150 caracteres")
    @Column(nullable = false, length = 150)
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    @Size(max = 100, message = "El autor admite máximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String autor;

    @Size(max = 100, message = "La editorial admite máximo 100 caracteres")
    @Column(length = 100)
    private String editorial;

    @Min(value = 1450, message = "El año de publicación no es válido")
    @Max(value = 2100, message = "El año de publicación no es válido")
    @Column(name = "anio_publicacion")
    private Integer anioPublicacion;

    // Ejemplares disponibles para préstamo. Baja al prestar y sube al devolver.
    @NotNull(message = "El número de ejemplares es obligatorio")
    @Min(value = 0, message = "Los ejemplares no pueden ser negativos")
    @Column(name = "num_ejemplares", nullable = false)
    private Integer numEjemplares;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }

    public Integer getAnioPublicacion() { return anioPublicacion; }
    public void setAnioPublicacion(Integer anioPublicacion) { this.anioPublicacion = anioPublicacion; }

    public Integer getNumEjemplares() { return numEjemplares; }
    public void setNumEjemplares(Integer numEjemplares) { this.numEjemplares = numEjemplares; }
}
