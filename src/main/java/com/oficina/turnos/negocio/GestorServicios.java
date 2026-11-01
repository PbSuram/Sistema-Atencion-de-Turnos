package com.oficina.turnos.negocio;

import com.oficina.turnos.datos.ServicioDAO;
import com.oficina.turnos.datos.TurnoDAO;
import com.oficina.turnos.excepciones.OperacionInvalidaException;
import com.oficina.turnos.modelo.EstadoGeneral;
import com.oficina.turnos.modelo.Servicio;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reglas de negocio de los servicios. Usamos un LinkedHashMap de Java como
 * colección adicional (índice de servicios por código); no sustituye a
 * la cola ni al BST propios.
 */
public class GestorServicios {
    private final ServicioDAO servicioDAO;
    private final TurnoDAO turnoDAO;
    private final Map<Integer, Servicio> serviciosPorCodigo = new LinkedHashMap<>();

    public GestorServicios(ServicioDAO servicioDAO, TurnoDAO turnoDAO) {
        this.servicioDAO = servicioDAO;
        this.turnoDAO = turnoDAO;
    }

    public void cargarDesdeBaseDeDatos() throws SQLException {
        serviciosPorCodigo.clear();
        for (Servicio servicio : servicioDAO.obtenerTodos()) {
            serviciosPorCodigo.put(servicio.getCodigo(), servicio);
        }
    }

    public void registrar(int codigo, String nombre) throws SQLException, OperacionInvalidaException {
        if (codigo <= 0) {
            throw new OperacionInvalidaException("El código debe ser un número entero positivo.");
        }
        validarNombre(nombre);
        if (servicioDAO.existeCodigo(codigo)) {
            throw new OperacionInvalidaException("Ya existe un servicio con el código " + codigo + ".");
        }

        Servicio servicio = new Servicio(codigo, nombre.trim(), EstadoGeneral.ACTIVO);
        servicioDAO.insertar(servicio);
        serviciosPorCodigo.put(codigo, servicio);
    }

    public Servicio buscar(int codigo) {
        return serviciosPorCodigo.get(codigo);
    }

    public List<Servicio> listarTodos() {
        return new ArrayList<>(serviciosPorCodigo.values());
    }

    public void mostrarTodos() {
        if (serviciosPorCodigo.isEmpty()) {
            System.out.println("No hay servicios registrados.");
            return;
        }
        for (Servicio servicio : serviciosPorCodigo.values()) {
            System.out.println(servicio);
        }
    }

    public void actualizarNombre(int codigo, String nuevoNombre)
            throws SQLException, OperacionInvalidaException {
        Servicio servicio = obtenerExistente(codigo);
        validarNombre(nuevoNombre);

        servicioDAO.actualizar(new Servicio(codigo, nuevoNombre.trim(), servicio.getEstado()));
        servicio.setNombre(nuevoNombre.trim());
    }

    public void cambiarEstado(int codigo, EstadoGeneral nuevoEstado)
            throws SQLException, OperacionInvalidaException {
        Servicio servicio = obtenerExistente(codigo);
        if (nuevoEstado == EstadoGeneral.INACTIVO && turnoDAO.tienePendientePorServicio(codigo)) {
            throw new OperacionInvalidaException("No se puede desactivar: el servicio tiene turnos pendientes.");
        }

        servicioDAO.actualizar(new Servicio(codigo, servicio.getNombre(), nuevoEstado));
        servicio.setEstado(nuevoEstado);
    }

    private Servicio obtenerExistente(int codigo) throws OperacionInvalidaException {
        Servicio servicio = serviciosPorCodigo.get(codigo);
        if (servicio == null) {
            throw new OperacionInvalidaException("No existe un servicio con el código " + codigo + ".");
        }
        return servicio;
    }

    private void validarNombre(String nombre) throws OperacionInvalidaException {
        if (nombre == null || nombre.isBlank()) {
            throw new OperacionInvalidaException("El nombre es obligatorio.");
        }
    }
}
