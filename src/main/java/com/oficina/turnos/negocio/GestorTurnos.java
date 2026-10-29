package com.oficina.turnos.negocio;

import com.oficina.turnos.datos.TurnoDAO;
import com.oficina.turnos.estructuras.ColaTurnos;
import com.oficina.turnos.excepciones.OperacionInvalidaException;
import com.oficina.turnos.modelo.Cliente;
import com.oficina.turnos.modelo.EstadoTurno;
import com.oficina.turnos.modelo.Servicio;
import com.oficina.turnos.modelo.Turno;
import com.oficina.turnos.reportes.ResumenServicio;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reglas de negocio de los turnos. Siempre sigue el mismo orden:
 * 1) validar, 2) guardar en SQLite, 3) solo si se guardó, tocar la cola.
 */
public class GestorTurnos {
    private final TurnoDAO turnoDAO;
    private final GestorClientes gestorClientes;
    private final GestorServicios gestorServicios;
    private final ColaTurnos cola = new ColaTurnos();

    public GestorTurnos(TurnoDAO turnoDAO, GestorClientes gestorClientes, GestorServicios gestorServicios) {
        this.turnoDAO = turnoDAO;
        this.gestorClientes = gestorClientes;
        this.gestorServicios = gestorServicios;
    }

    /** Reconstruye la cola al iniciar con los turnos PENDIENTE, ordenados por número. */
    public void cargarDesdeBaseDeDatos() throws SQLException {
        while (!cola.estaVacia()) {
            cola.desencolar();
        }
        for (Turno turno : turnoDAO.obtenerPorEstado(EstadoTurno.PENDIENTE)) {
            cola.encolar(turno);
        }
    }

    public Turno generarTurno(int codigoCliente, int codigoServicio)
            throws SQLException, OperacionInvalidaException {
        // 1) Validar
        Cliente cliente = gestorClientes.buscar(codigoCliente);
        if (cliente == null) {
            throw new OperacionInvalidaException("No existe un cliente con el código " + codigoCliente + ".");
        }
        if (!cliente.estaActivo()) {
            throw new OperacionInvalidaException("El cliente " + codigoCliente + " está inactivo.");
        }
        Servicio servicio = gestorServicios.buscar(codigoServicio);
        if (servicio == null) {
            throw new OperacionInvalidaException("No existe un servicio con el código " + codigoServicio + ".");
        }
        if (!servicio.estaActivo()) {
            throw new OperacionInvalidaException("El servicio " + codigoServicio + " está inactivo.");
        }
        if (cola.tieneTurnoDeCliente(codigoCliente)) {
            throw new OperacionInvalidaException("El cliente " + codigoCliente + " ya tiene un turno pendiente.");
        }

        // 2) Guardar en SQLite (si falla, lanza SQLException y la cola no cambia)
        LocalDateTime ahora = LocalDateTime.now().withNano(0);
        int numero = turnoDAO.insertar(codigoCliente, codigoServicio, ahora);

        // 3) Actualizar la memoria
        Turno turno = new Turno(numero, codigoCliente, codigoServicio, ahora, EstadoTurno.PENDIENTE, null, null);
        cola.encolar(turno);
        return turno;
    }

    public Turno atenderSiguiente() throws SQLException, OperacionInvalidaException {
        if (cola.estaVacia()) {
            throw new OperacionInvalidaException("No existen turnos pendientes.");
        }
        Turno turno = cola.verFrente();               // se mira sin retirarlo
        LocalDateTime ahora = LocalDateTime.now().withNano(0);
        turnoDAO.marcarAtendido(turno.getNumero(), ahora);
        cola.desencolar();                            // solo si se guardó
        turno.marcarAtendido(ahora);
        return turno;
    }

    public Turno cancelarTurno(int numero) throws SQLException, OperacionInvalidaException {
        Turno turno = cola.buscar(numero);
        if (turno == null) {
            Turno enBase = turnoDAO.buscarPorNumero(numero);
            if (enBase == null) {
                throw new OperacionInvalidaException("No existe el turno " + numero + ".");
            }
            throw new OperacionInvalidaException("El turno " + numero + " no se puede cancelar porque está "
                    + enBase.getEstado() + ".");
        }
        LocalDateTime ahora = LocalDateTime.now().withNano(0);
        turnoDAO.marcarCancelado(numero, ahora);
        cola.cancelar(numero);
        turno.marcarCancelado(ahora);
        return turno;
    }

    /** Turnos pendientes en orden de atención (los toma de la cola propia). */
    public List<Turno> obtenerPendientes() {
        return cola.recorrer();
    }

    public List<Turno> obtenerAtendidos() throws SQLException {
        return turnoDAO.obtenerPorEstado(EstadoTurno.ATENDIDO);
    }

    public List<Turno> obtenerCancelados() throws SQLException {
        return turnoDAO.obtenerPorEstado(EstadoTurno.CANCELADO);
    }

    public List<Turno> obtenerHistorialCliente(int codigoCliente) throws SQLException, OperacionInvalidaException {
        if (gestorClientes.buscar(codigoCliente) == null) {
            throw new OperacionInvalidaException("No existe un cliente con el código " + codigoCliente + ".");
        }
        return turnoDAO.obtenerPorCliente(codigoCliente);
    }

    /**
     * Resumen por servicio. Primero crea una fila por cada servicio (así los
     * que no tienen turnos aparecen en cero) y luego cuenta cada turno.
     * El LinkedHashMap es la colección de Java que agrupa por código.
     */
    public List<ResumenServicio> obtenerResumenPorServicio() throws SQLException {
        Map<Integer, ResumenServicio> resumen = new LinkedHashMap<>();
        for (Servicio servicio : gestorServicios.listarTodos()) {
            resumen.put(servicio.getCodigo(), new ResumenServicio(servicio.getNombre()));
        }
        for (Turno turno : turnoDAO.obtenerTodos()) {
            ResumenServicio fila = resumen.get(turno.getCodigoServicio());
            if (fila != null) {
                fila.contar(turno.getEstado());
            }
        }
        return new ArrayList<>(resumen.values());
    }

    /** Nombre del cliente para mostrar en pantalla. */
    public String nombreCliente(int codigo) {
        Cliente cliente = gestorClientes.buscar(codigo);
        return cliente == null ? "(desconocido)" : cliente.getNombre();
    }

    /** Nombre del servicio para mostrar en pantalla. */
    public String nombreServicio(int codigo) {
        Servicio servicio = gestorServicios.buscar(codigo);
        return servicio == null ? "(desconocido)" : servicio.getNombre();
    }
}
