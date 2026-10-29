package com.oficina.turnos.excepciones;

/**
 * Señala el incumplimiento de una regla de negocio (cliente inactivo,
 * turno ya atendido, código duplicado, etc.). Se distingue de
 * SQLException, que representa fallas técnicas de la base de datos.
 */
public class OperacionInvalidaException extends Exception {
    private static final long serialVersionUID = 1L;

    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
