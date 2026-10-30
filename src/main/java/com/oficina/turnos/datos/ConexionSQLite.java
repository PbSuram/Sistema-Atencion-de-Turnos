package com.oficina.turnos.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Punto único de acceso a la conexión JDBC con SQLite. Usa una ruta
 * relativa (data/turnos.db) para no depender de ninguna carpeta
 * particular de la computadora de un integrante.
 */
public final class ConexionSQLite {
    private static final String URL = "jdbc:sqlite:data/turnos.db";

    private ConexionSQLite() {
    }

    /** Abre una conexión con las claves foráneas habilitadas. */
    public static Connection obtenerConexion() throws SQLException {
        Connection conexion = DriverManager.getConnection(URL);
        try (Statement statement = conexion.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            conexion.close();
            throw e;
        }
        return conexion;
    }
}
