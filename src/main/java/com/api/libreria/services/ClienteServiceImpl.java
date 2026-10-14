package com.api.libreria.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.libreria.exceptions.RecursoNoEncontradoException;
import com.api.libreria.exceptions.ReglaNegocioException;
import com.api.libreria.models.ClienteModel;
import com.api.libreria.repositories.IClienteRepository;
import com.api.libreria.repositories.IPrestamoRepository;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final IClienteRepository clienteRepository;
    private final IPrestamoRepository prestamoRepository;

    public ClienteServiceImpl(IClienteRepository clienteRepository, IPrestamoRepository prestamoRepository) {
        this.clienteRepository = clienteRepository;
        this.prestamoRepository = prestamoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteModel> listarClientes() {
        return clienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteModel obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", id));
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteModel obtenerPorCorreo(String correo) {
        return clienteRepository.findByCorreoElectronicoIgnoreCase(correo.trim())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente con correo " + correo + " no encontrado"));
    }

    @Override
    @Transactional
    public ClienteModel registrarCliente(ClienteModel cliente) {
        String correo = normalizarCorreo(cliente.getCorreoElectronico());
        if (clienteRepository.existsByCorreoElectronicoIgnoreCase(correo)) {
            throw new ReglaNegocioException("El correo " + correo + " ya está registrado");
        }
        cliente.setCorreoElectronico(correo);
        cliente.setId(null);
        cliente.setFechaRegistro(null); // se asigna automáticamente
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public ClienteModel actualizarCliente(Long id, ClienteModel datos) {
        ClienteModel cliente = obtenerPorId(id);
        String correo = normalizarCorreo(datos.getCorreoElectronico());
        if (clienteRepository.existsByCorreoElectronicoIgnoreCaseAndIdNot(correo, id)) {
            throw new ReglaNegocioException("El correo " + correo + " ya está registrado por otro cliente");
        }
        cliente.setNombre(datos.getNombre());
        cliente.setApellidos(datos.getApellidos());
        cliente.setCorreoElectronico(correo);
        cliente.setTelefono(datos.getTelefono());
        cliente.setDireccion(datos.getDireccion());
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public void eliminarCliente(Long id) {
        ClienteModel cliente = obtenerPorId(id);
        if (prestamoRepository.existsByClienteId(id)) {
            throw new ReglaNegocioException("No se puede eliminar a " + cliente.getNombreCompleto()
                    + " porque tiene préstamos registrados");
        }
        clienteRepository.delete(cliente);
    }

    private String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase();
    }
}
