package com.api.libreria.exceptions;

/**
 * Se lanza cuando la petición es válida pero viola una regla de la biblioteca
 * (correo repetido, libro sin ejemplares, préstamo ya devuelto...). Se traduce a HTTP 409.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
