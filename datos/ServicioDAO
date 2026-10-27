package com.oficina.turnos.datos;

import com.oficina.turnos.modelo.Servicio;

import java.sql.SQLException;
import java.util.List;

public interface ServicioDAO {
    List<Servicio> obtenerTodos() throws SQLException;

    boolean existeCodigo(int codigo) throws SQLException;

    void insertar(Servicio servicio) throws SQLException;

    void actualizar(Servicio servicio) throws SQLException;
}
