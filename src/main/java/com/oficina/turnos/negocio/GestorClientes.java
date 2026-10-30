package com.oficina.turnos.negocio;

import com.oficina.turnos.datos.ClienteDAO;
import com.oficina.turnos.datos.TurnoDAO;
import com.oficina.turnos.estructuras.ArbolClientes;
import com.oficina.turnos.excepciones.OperacionInvalidaException;
import com.oficina.turnos.modelo.Cliente;
import com.oficina.turnos.modelo.EstadoGeneral;

import java.sql.SQLException;
import java.util.List;

/**
 * Reglas de negocio de los clientes. Patrón de cada operación:
 * validar -> guardar en SQLite -> solo si funcionó, actualizar el BST.
 */
public class GestorClientes {
    private static final String PATRON_TELEFONO = "\\d{8}";

    private final ClienteDAO clienteDAO;
    private final TurnoDAO turnoDAO;
    private final ArbolClientes arbol = new ArbolClientes();

    public GestorClientes(ClienteDAO clienteDAO, TurnoDAO turnoDAO) {
        this.clienteDAO = clienteDAO;
        this.turnoDAO = turnoDAO;
    }

    /** Al iniciar: reconstruye el BST con todos los clientes (activos e inactivos). */
    public void cargarDesdeBaseDeDatos() throws SQLException {
        arbol.limpiar();
        for (Cliente cliente : clienteDAO.obtenerTodos()) {
            arbol.insertar(cliente);
        }
    }

    public void registrar(int codigo, String nombre, String telefono)
            throws SQLException, OperacionInvalidaException {
        if (codigo <= 0) {
            throw new OperacionInvalidaException("El código debe ser un número entero positivo.");
        }
        validarNombre(nombre);
        validarTelefono(telefono);
        if (arbol.buscar(codigo) != null) {
            throw new OperacionInvalidaException("Ya existe un cliente con el código " + codigo + ".");
        }

        Cliente cliente = new Cliente(codigo, nombre.trim(), telefono.trim(), EstadoGeneral.ACTIVO);
        clienteDAO.insertar(cliente);   // si falla, lanza excepción y el árbol no se toca
        arbol.insertar(cliente);
    }

    /** Búsqueda mediante el BST. Devuelve null si no existe. */
    public Cliente buscar(int codigo) {
        return arbol.buscar(codigo);
    }

    /** Todos los clientes ordenados por código (recorrido inorden). */
    public List<Cliente> listarTodos() {
        return arbol.recorrerInorden();
    }

    public void mostrarTodos() {
        if (arbol.estaVacio()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        for (Cliente cliente : arbol.recorrerInorden()) {
            System.out.println(cliente);
        }
    }

    public void actualizarDatos(int codigo, String nuevoNombre, String nuevoTelefono)
            throws SQLException, OperacionInvalidaException {
        Cliente cliente = obtenerExistente(codigo);
        validarNombre(nuevoNombre);
        validarTelefono(nuevoTelefono);

        // Se guarda una copia; el objeto del árbol solo cambia si SQLite confirma.
        clienteDAO.actualizar(new Cliente(codigo, nuevoNombre.trim(), nuevoTelefono.trim(), cliente.getEstado()));
        cliente.setNombre(nuevoNombre.trim());
        cliente.setTelefono(nuevoTelefono.trim());
    }

    public void cambiarEstado(int codigo, EstadoGeneral nuevoEstado)
            throws SQLException, OperacionInvalidaException {
        Cliente cliente = obtenerExistente(codigo);
        if (cliente.getEstado() == nuevoEstado) {
            throw new OperacionInvalidaException("El cliente ya está " + nuevoEstado + ".");
        }
        if (nuevoEstado == EstadoGeneral.INACTIVO && turnoDAO.tienePendientePorCliente(codigo)) {
            throw new OperacionInvalidaException("No se puede desactivar: el cliente tiene un turno pendiente.");
        }

        clienteDAO.actualizar(new Cliente(codigo, cliente.getNombre(), cliente.getTelefono(), nuevoEstado));
        cliente.setEstado(nuevoEstado);
    }

    private Cliente obtenerExistente(int codigo) throws OperacionInvalidaException {
        Cliente cliente = arbol.buscar(codigo);
        if (cliente == null) {
            throw new OperacionInvalidaException("No existe un cliente con el código " + codigo + ".");
        }
        return cliente;
    }

    private void validarNombre(String nombre) throws OperacionInvalidaException {
        if (nombre == null || nombre.isBlank()) {
            throw new OperacionInvalidaException("El nombre es obligatorio.");
        }
    }

    private void validarTelefono(String telefono) throws OperacionInvalidaException {
        if (telefono == null || !telefono.trim().matches(PATRON_TELEFONO)) {
            throw new OperacionInvalidaException("El teléfono debe tener exactamente 8 dígitos.");
        }
    }
}
