package com.api.libreria.services;

import java.util.List;

import com.api.libreria.models.ClienteModel;

/** Servicio de entidad: socios de la biblioteca. */
public interface ClienteService {

    List<ClienteModel> listarClientes();

    ClienteModel obtenerPorId(Long id);

    ClienteModel obtenerPorCorreo(String correo);

    ClienteModel registrarCliente(ClienteModel cliente);

    ClienteModel actualizarCliente(Long id, ClienteModel datos);

    void eliminarCliente(Long id);
}
