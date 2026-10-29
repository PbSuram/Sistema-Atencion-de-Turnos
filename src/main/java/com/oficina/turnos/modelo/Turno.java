package com.oficina.turnos.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Un turno solicitado por un cliente para un servicio.
 * El número lo asigna SQLite (AUTOINCREMENT), por eso no tiene setter.
 */
public class Turno {
    public static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final int numero;
    private final int codigoCliente;
    private final int codigoServicio;
    private final LocalDateTime fechaGeneracion;
    private EstadoTurno estado;
    private LocalDateTime fechaAtencion;      // null hasta que se atienda
    private LocalDateTime fechaCancelacion;   // null hasta que se cancele

    public Turno(int numero, int codigoCliente, int codigoServicio, LocalDateTime fechaGeneracion,
                 EstadoTurno estado, LocalDateTime fechaAtencion, LocalDateTime fechaCancelacion) {
        this.numero = numero;
        this.codigoCliente = codigoCliente;
        this.codigoServicio = codigoServicio;
        this.fechaGeneracion = fechaGeneracion;
        this.estado = estado;
        this.fechaAtencion = fechaAtencion;
        this.fechaCancelacion = fechaCancelacion;
    }

    public int getNumero() { return numero; }
    public int getCodigoCliente() { return codigoCliente; }
    public int getCodigoServicio() { return codigoServicio; }
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public EstadoTurno getEstado() { return estado; }
    public LocalDateTime getFechaAtencion() { return fechaAtencion; }
    public LocalDateTime getFechaCancelacion() { return fechaCancelacion; }

    public boolean estaPendiente() {
        return estado == EstadoTurno.PENDIENTE;
    }

    /** Se llama solo después de que SQLite confirmó el cambio. */
    public void marcarAtendido(LocalDateTime fecha) {
        this.estado = EstadoTurno.ATENDIDO;
        this.fechaAtencion = fecha;
    }

    /** Se llama solo después de que SQLite confirmó el cambio. */
    public void marcarCancelado(LocalDateTime fecha) {
        this.estado = EstadoTurno.CANCELADO;
        this.fechaCancelacion = fecha;
    }

    @Override
    public String toString() {
        String texto = "Turno #" + numero + " | Cliente " + codigoCliente + " | Servicio " + codigoServicio
                + " | Generado: " + fechaGeneracion.format(FORMATO_FECHA) + " | " + estado;
        if (fechaAtencion != null) {
            texto += " | Atendido: " + fechaAtencion.format(FORMATO_FECHA);
        }
        if (fechaCancelacion != null) {
            texto += " | Cancelado: " + fechaCancelacion.format(FORMATO_FECHA);
        }
        return texto;
    }
}
