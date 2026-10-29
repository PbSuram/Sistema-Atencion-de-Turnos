package com.oficina.turnos.datos;

import com.oficina.turnos.modelo.EstadoGeneral;
import com.oficina.turnos.modelo.Servicio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAOSQLite implements ServicioDAO {

    @Override
    public List<Servicio> obtenerTodos() throws SQLException {
        String sql = "SELECT codigo, nombre, estado FROM servicios ORDER BY codigo";
        List<Servicio> servicios = new ArrayList<>();
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                servicios.add(new Servicio(
                        rs.getInt("codigo"),
                        rs.getString("nombre"),
                        EstadoGeneral.valueOf(rs.getString("estado"))));
            }
        }
        return servicios;
    }

    @Override
    public boolean existeCodigo(int codigo) throws SQLException {
        String sql = "SELECT 1 FROM servicios WHERE codigo = ?";
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public void insertar(Servicio servicio) throws SQLException {
        String sql = "INSERT INTO servicios (codigo, nombre, estado) VALUES (?, ?, ?)";
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, servicio.getCodigo());
            ps.setString(2, servicio.getNombre());
            ps.setString(3, servicio.getEstado().name());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Servicio servicio) throws SQLException {
        String sql = "UPDATE servicios SET nombre = ?, estado = ? WHERE codigo = ?";
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, servicio.getNombre());
            ps.setString(2, servicio.getEstado().name());
            ps.setInt(3, servicio.getCodigo());
            ps.executeUpdate();
        }
    }
}
