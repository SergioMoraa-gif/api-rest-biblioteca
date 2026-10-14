package com.api.libreria.exceptions;

/** Se lanza cuando un id no existe. Se traduce a HTTP 404. */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Object id) {
        super(recurso + " con id " + id + " no encontrado");
    }

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
