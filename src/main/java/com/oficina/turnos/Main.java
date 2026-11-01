package com.oficina.turnos;

import com.oficina.turnos.datos.ClienteDAOSQLite;
import com.oficina.turnos.datos.ServicioDAOSQLite;
import com.oficina.turnos.datos.TurnoDAO;
import com.oficina.turnos.datos.TurnoDAOSQLite;
import com.oficina.turnos.negocio.GestorClientes;
import com.oficina.turnos.negocio.GestorServicios;
import com.oficina.turnos.negocio.GestorTurnos;
import com.oficina.turnos.vista.MenuConsola;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

/**
 * Punto de entrada. Solo arma las piezas, reconstruye el BST y la cola
 * desde SQLite y le pasa el control al menú.
 */
public class Main {
    public static void main(String[] args) {
        // Si la base no existe, SQLite crearía una vacía; mejor avisar.
        if (!Files.exists(Path.of("data", "turnos.db"))) {
            System.out.println("No se encontró data/turnos.db. Créela con sql/crear_y_cargar.sql (ver README)");
            System.out.println("y ejecute el programa desde la carpeta del proyecto.");
            return;
        }

        TurnoDAO turnoDAO = new TurnoDAOSQLite();
        GestorClientes gestorClientes = new GestorClientes(new ClienteDAOSQLite(), turnoDAO);
        GestorServicios gestorServicios = new GestorServicios(new ServicioDAOSQLite(), turnoDAO);
        GestorTurnos gestorTurnos = new GestorTurnos(turnoDAO, gestorClientes, gestorServicios);

        try {
            gestorClientes.cargarDesdeBaseDeDatos();   // construye el BST
            gestorServicios.cargarDesdeBaseDeDatos();
            gestorTurnos.cargarDesdeBaseDeDatos();     // reconstruye la cola
        } catch (SQLException e) {
            System.out.println("No se pudo abrir la base de datos data/turnos.db: " + e.getMessage());
            System.out.println("Verifique que exista (ver README) y que ejecute el programa desde la carpeta del proyecto.");
            return;
        }

        new MenuConsola(gestorClientes, gestorServicios, gestorTurnos).iniciar();
    }
}
