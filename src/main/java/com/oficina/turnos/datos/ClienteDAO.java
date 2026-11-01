package com.oficina.turnos.datos;

import com.oficina.turnos.modelo.Cliente;

import java.sql.SQLException;
import java.util.List;

/** Operaciones de persistencia de clientes (interfaz + implementación SQLite). */
public interface ClienteDAO {
    List<Cliente> obtenerTodos() throws SQLException;

    boolean existeCodigo(int codigo) throws SQLException;

    void insertar(Cliente cliente) throws SQLException;

    /** Actualiza nombre, teléfono y estado. El código no se modifica. */
    void actualizar(Cliente cliente) throws SQLException;
}
