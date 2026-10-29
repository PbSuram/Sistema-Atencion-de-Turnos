package com.oficina.turnos.datos;

import com.oficina.turnos.modelo.EstadoTurno;
import com.oficina.turnos.modelo.Turno;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/** Operaciones de base de datos sobre la tabla turnos. */
public interface TurnoDAO {

    /** Inserta un turno PENDIENTE y devuelve el número que asignó SQLite. */
    int insertar(int codigoCliente, int codigoServicio, LocalDateTime fechaGeneracion) throws SQLException;

    void marcarAtendido(int numero, LocalDateTime fecha) throws SQLException;

    void marcarCancelado(int numero, LocalDateTime fecha) throws SQLException;

    /** Busca un turno por número en la base, o devuelve null. */
    Turno buscarPorNumero(int numero) throws SQLException;

    /** Turnos con ese estado, ordenados por número. */
    List<Turno> obtenerPorEstado(EstadoTurno estado) throws SQLException;

    List<Turno> obtenerPorCliente(int codigoCliente) throws SQLException;

    List<Turno> obtenerTodos() throws SQLException;

    boolean tienePendientePorCliente(int codigoCliente) throws SQLException;

    boolean tienePendientePorServicio(int codigoServicio) throws SQLException;
}
