package com.oficina.turnos.vista;

import com.oficina.turnos.excepciones.OperacionInvalidaException;
import com.oficina.turnos.modelo.Cliente;
import com.oficina.turnos.modelo.EstadoGeneral;
import com.oficina.turnos.modelo.Turno;
import com.oficina.turnos.negocio.GestorClientes;
import com.oficina.turnos.negocio.GestorServicios;
import com.oficina.turnos.negocio.GestorTurnos;
import com.oficina.turnos.reportes.ResumenServicio;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Vista de consola: solo pide datos y muestra resultados.
 * Las reglas viven en los gestores; aquí únicamente se capturan sus errores.
 */
public class MenuConsola {
    private static final String NOMBRE_OFICINA = "Oficina de Servicios";

    private final GestorClientes gestorClientes;
    private final GestorServicios gestorServicios;
    private final GestorTurnos gestorTurnos;
    private final Scanner entrada = new Scanner(System.in);

    public MenuConsola(GestorClientes gestorClientes, GestorServicios gestorServicios, GestorTurnos gestorTurnos) {
        this.gestorClientes = gestorClientes;
        this.gestorServicios = gestorServicios;
        this.gestorTurnos = gestorTurnos;
    }

    // ================================================================ menú principal

    public void iniciar() {
        boolean salir = false;
        while (!salir) {
            System.out.println();
            System.out.println("======= " + NOMBRE_OFICINA + " - Sistema de Turnos =======");
            System.out.println(" 1. Gestión de clientes");
            System.out.println(" 2. Gestión de servicios");
            System.out.println(" 3. Generar turno");
            System.out.println(" 4. Atender siguiente turno");
            System.out.println(" 5. Cancelar turno");
            System.out.println(" 6. Turnos pendientes");
            System.out.println(" 7. Turnos atendidos");
            System.out.println(" 8. Turnos cancelados");
            System.out.println(" 9. Historial de un cliente");
            System.out.println("10. Resumen por servicio");
            System.out.println(" 0. Salir");

            int opcion = leerEntero("Seleccione una opción: ");
            try {
                switch (opcion) {
                    case 1 -> menuClientes();
                    case 2 -> menuServicios();
                    case 3 -> generarTurno();
                    case 4 -> atenderSiguiente();
                    case 5 -> cancelarTurno();
                    case 6 -> mostrarTurnos("TURNOS PENDIENTES (en orden de atención)", gestorTurnos.obtenerPendientes());
                    case 7 -> mostrarTurnos("TURNOS ATENDIDOS", gestorTurnos.obtenerAtendidos());
                    case 8 -> mostrarTurnos("TURNOS CANCELADOS", gestorTurnos.obtenerCancelados());
                    case 9 -> historialCliente();
                    case 10 -> resumenPorServicio();
                    case 0 -> salir = true;
                    default -> System.out.println("Opción inexistente. Intente de nuevo.");
                }
            } catch (OperacionInvalidaException e) {
                System.out.println("Aviso: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            }
        }
        System.out.println("¡Hasta pronto!");
    }

    // ================================================================ clientes

    private void menuClientes() throws SQLException, OperacionInvalidaException {
        System.out.println();
        System.out.println("--- Gestión de clientes ---");
        System.out.println("1. Registrar cliente");
        System.out.println("2. Consultar todos");
        System.out.println("3. Buscar por código");
        System.out.println("4. Actualizar nombre y teléfono");
        System.out.println("5. Activar / desactivar");
        System.out.println("0. Regresar");

        switch (leerEntero("Opción: ")) {
            case 1 -> {
                int codigo = leerEntero("Código: ");
                String nombre = leerTexto("Nombre completo: ");
                String telefono = leerTexto("Teléfono (8 dígitos): ");
                gestorClientes.registrar(codigo, nombre, telefono);
                System.out.println("Cliente registrado.");
            }
            case 2 -> gestorClientes.mostrarTodos();
            case 3 -> {
                Cliente cliente = gestorClientes.buscar(leerEntero("Código: "));
                System.out.println(cliente == null ? "No existe un cliente con ese código." : cliente);
            }
            case 4 -> {
                int codigo = leerEntero("Código: ");
                String nombre = leerTexto("Nuevo nombre: ");
                String telefono = leerTexto("Nuevo teléfono: ");
                gestorClientes.actualizarDatos(codigo, nombre, telefono);
                System.out.println("Cliente actualizado.");
            }
            case 5 -> {
                int codigo = leerEntero("Código: ");
                gestorClientes.cambiarEstado(codigo, leerEstado());
                System.out.println("Estado actualizado.");
            }
            case 0 -> { }
            default -> System.out.println("Opción inexistente.");
        }
    }

    // ================================================================ servicios

    private void menuServicios() throws SQLException, OperacionInvalidaException {
        System.out.println();
        System.out.println("--- Gestión de servicios ---");
        System.out.println("1. Registrar servicio");
        System.out.println("2. Listar servicios");
        System.out.println("3. Actualizar nombre");
        System.out.println("4. Activar / desactivar");
        System.out.println("0. Regresar");

        switch (leerEntero("Opción: ")) {
            case 1 -> {
                int codigo = leerEntero("Código: ");
                gestorServicios.registrar(codigo, leerTexto("Nombre: "));
                System.out.println("Servicio registrado.");
            }
            case 2 -> gestorServicios.mostrarTodos();
            case 3 -> {
                int codigo = leerEntero("Código: ");
                gestorServicios.actualizarNombre(codigo, leerTexto("Nuevo nombre: "));
                System.out.println("Servicio actualizado.");
            }
            case 4 -> {
                int codigo = leerEntero("Código: ");
                gestorServicios.cambiarEstado(codigo, leerEstado());
                System.out.println("Estado actualizado.");
            }
            case 0 -> { }
            default -> System.out.println("Opción inexistente.");
        }
    }

    // ================================================================ turnos

    private void generarTurno() throws SQLException, OperacionInvalidaException {
        int codigoCliente = leerEntero("Código del cliente: ");
        int codigoServicio = leerEntero("Código del servicio: ");
        Turno turno = gestorTurnos.generarTurno(codigoCliente, codigoServicio);
        System.out.println("Turno generado: #" + turno.getNumero() + " para "
                + gestorTurnos.nombreCliente(codigoCliente) + " - " + gestorTurnos.nombreServicio(codigoServicio));
    }

    private void atenderSiguiente() throws SQLException, OperacionInvalidaException {
        Turno turno = gestorTurnos.atenderSiguiente();
        System.out.println("Atendiendo turno #" + turno.getNumero());
        System.out.println("  Cliente : " + turno.getCodigoCliente() + " - " + gestorTurnos.nombreCliente(turno.getCodigoCliente()));
        System.out.println("  Servicio: " + turno.getCodigoServicio() + " - " + gestorTurnos.nombreServicio(turno.getCodigoServicio()));
    }

    private void cancelarTurno() throws SQLException, OperacionInvalidaException {
        Turno turno = gestorTurnos.cancelarTurno(leerEntero("Número de turno a cancelar: "));
        System.out.println("Turno #" + turno.getNumero() + " cancelado.");
    }

    private void historialCliente() throws SQLException, OperacionInvalidaException {
        int codigo = leerEntero("Código del cliente: ");
        List<Turno> historial = gestorTurnos.obtenerHistorialCliente(codigo);
        mostrarTurnos("HISTORIAL DE " + gestorTurnos.nombreCliente(codigo).toUpperCase(), historial);
    }

    private void resumenPorServicio() throws SQLException {
        List<ResumenServicio> filas = gestorTurnos.obtenerResumenPorServicio();
        System.out.println();
        System.out.println(String.format("%-26s %10s %9s %10s %7s", "Servicio", "Pendientes", "Atendidos", "Cancelados", "Total"));
        int pendientes = 0, atendidos = 0, cancelados = 0;
        for (ResumenServicio fila : filas) {
            System.out.println(fila);
            pendientes += fila.getPendientes();
            atendidos += fila.getAtendidos();
            cancelados += fila.getCancelados();
        }
        System.out.println(String.format("%-26s %10d %9d %10d %7d", "TOTAL",
                pendientes, atendidos, cancelados, pendientes + atendidos + cancelados));
    }

    private void mostrarTurnos(String titulo, List<Turno> turnos) {
        System.out.println();
        System.out.println("--- " + titulo + " ---");
        if (turnos.isEmpty()) {
            System.out.println("No hay turnos para mostrar.");
            return;
        }
        for (Turno turno : turnos) {
            System.out.println(turno);
        }
    }

    // ================================================================ lectura de datos

    /** Pide un número entero hasta que el usuario escriba uno válido. */
    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = entrada.nextLine().trim();
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número entero válido.");
            }
        }
    }

    /** Pide un texto que no esté vacío. */
    private String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = entrada.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("Este campo no puede quedar vacío.");
        }
    }

    private EstadoGeneral leerEstado() {
        while (true) {
            int opcion = leerEntero("1 = Activar, 2 = Desactivar: ");
            if (opcion == 1) return EstadoGeneral.ACTIVO;
            if (opcion == 2) return EstadoGeneral.INACTIVO;
            System.out.println("Opción inexistente.");
        }
    }
}
