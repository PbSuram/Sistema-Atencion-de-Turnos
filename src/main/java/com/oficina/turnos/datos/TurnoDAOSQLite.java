package com.oficina.turnos.datos;

import com.oficina.turnos.modelo.EstadoTurno;
import com.oficina.turnos.modelo.Turno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Implementación de TurnoDAO con JDBC y SQLite. */
public class TurnoDAOSQLite implements TurnoDAO {

    private static final String COLUMNAS =
            "numero, codigo_cliente, codigo_servicio, fecha_generacion, estado, fecha_atencion, fecha_cancelacion";

    @Override
    public int insertar(int codigoCliente, int codigoServicio, LocalDateTime fechaGeneracion) throws SQLException {
        String sql = "INSERT INTO turnos (codigo_cliente, codigo_servicio, fecha_generacion, estado) "
                + "VALUES (?, ?, ?, 'PENDIENTE')";
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, codigoCliente);
            ps.setInt(2, codigoServicio);
            ps.setString(3, fechaGeneracion.format(Turno.FORMATO_FECHA));
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getInt(1);   // número asignado por AUTOINCREMENT
                }
            }
        }
        throw new SQLException("SQLite no devolvió el número del turno.");
    }

    @Override
    public void marcarAtendido(int numero, LocalDateTime fecha) throws SQLException {
        cambiarEstado(numero, "ATENDIDO", "fecha_atencion", fecha);
    }

    @Override
    public void marcarCancelado(int numero, LocalDateTime fecha) throws SQLException {
        cambiarEstado(numero, "CANCELADO", "fecha_cancelacion", fecha);
    }

    /** Solo cambia turnos que sigan PENDIENTE; si no cambia ninguno, lanza error. */
    private void cambiarEstado(int numero, String nuevoEstado, String columnaFecha, LocalDateTime fecha)
            throws SQLException {
        String sql = "UPDATE turnos SET estado = ?, " + columnaFecha + " = ? "
                + "WHERE numero = ? AND estado = 'PENDIENTE'";
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setString(2, fecha.format(Turno.FORMATO_FECHA));
            ps.setInt(3, numero);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se actualizó el turno " + numero + ".");
            }
        }
    }

    @Override
    public Turno buscarPorNumero(int numero) throws SQLException {
        List<Turno> resultado = consultar("SELECT " + COLUMNAS + " FROM turnos WHERE numero = ?", numero);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    @Override
    public List<Turno> obtenerPorEstado(EstadoTurno estado) throws SQLException {
        return consultar("SELECT " + COLUMNAS + " FROM turnos WHERE estado = ? ORDER BY numero", estado.name());
    }

    @Override
    public List<Turno> obtenerPorCliente(int codigoCliente) throws SQLException {
        return consultar("SELECT " + COLUMNAS + " FROM turnos WHERE codigo_cliente = ? ORDER BY numero",
                codigoCliente);
    }

    @Override
    public List<Turno> obtenerTodos() throws SQLException {
        return consultar("SELECT " + COLUMNAS + " FROM turnos ORDER BY numero");
    }

    @Override
    public boolean tienePendientePorCliente(int codigoCliente) throws SQLException {
        return existe("SELECT 1 FROM turnos WHERE codigo_cliente = ? AND estado = 'PENDIENTE'", codigoCliente);
    }

    @Override
    public boolean tienePendientePorServicio(int codigoServicio) throws SQLException {
        return existe("SELECT 1 FROM turnos WHERE codigo_servicio = ? AND estado = 'PENDIENTE'", codigoServicio);
    }

    // ----------------------------------------------------------- auxiliares

    private boolean existe(String sql, int parametro) throws SQLException {
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Ejecuta un SELECT con parámetros opcionales y convierte cada fila en Turno. */
    private List<Turno> consultar(String sql, Object... parametros) throws SQLException {
        List<Turno> turnos = new ArrayList<>();
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    turnos.add(convertirFila(rs));
                }
            }
        }
        return turnos;
    }

    private Turno convertirFila(ResultSet rs) throws SQLException {
        return new Turno(
                rs.getInt("numero"),
                rs.getInt("codigo_cliente"),
                rs.getInt("codigo_servicio"),
                leerFecha(rs.getString("fecha_generacion")),
                EstadoTurno.valueOf(rs.getString("estado")),
                leerFecha(rs.getString("fecha_atencion")),
                leerFecha(rs.getString("fecha_cancelacion")));
    }

    private LocalDateTime leerFecha(String texto) {
        return texto == null ? null : LocalDateTime.parse(texto, Turno.FORMATO_FECHA);
    }
}
