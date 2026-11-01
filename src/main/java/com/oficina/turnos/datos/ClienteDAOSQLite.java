package com.oficina.turnos.datos;

import com.oficina.turnos.modelo.Cliente;
import com.oficina.turnos.modelo.EstadoGeneral;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOSQLite implements ClienteDAO {

    @Override
    public List<Cliente> obtenerTodos() throws SQLException {
        String sql = "SELECT codigo, nombre, telefono, estado FROM clientes ORDER BY codigo";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clientes.add(new Cliente(
                        rs.getInt("codigo"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        EstadoGeneral.valueOf(rs.getString("estado"))));
            }
        }
        return clientes;
    }

    @Override
    public boolean existeCodigo(int codigo) throws SQLException {
        String sql = "SELECT 1 FROM clientes WHERE codigo = ?";
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public void insertar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (codigo, nombre, telefono, estado) VALUES (?, ?, ?, ?)";
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, cliente.getCodigo());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getTelefono());
            ps.setString(4, cliente.getEstado().name());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET nombre = ?, telefono = ?, estado = ? WHERE codigo = ?";
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getEstado().name());
            ps.setInt(4, cliente.getCodigo());
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se actualizó ningún cliente con código " + cliente.getCodigo() + ".");
            }
        }
    }
}
