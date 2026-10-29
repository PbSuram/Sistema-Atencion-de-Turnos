package com.oficina.turnos.reportes;

import com.oficina.turnos.modelo.EstadoTurno;

/** Conteo de turnos de un servicio, por estado. */
public class ResumenServicio {
    private final String nombreServicio;
    private int pendientes;
    private int atendidos;
    private int cancelados;

    public ResumenServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public void contar(EstadoTurno estado) {
        switch (estado) {
            case PENDIENTE -> pendientes++;
            case ATENDIDO -> atendidos++;
            case CANCELADO -> cancelados++;
        }
    }

    public int getPendientes() { return pendientes; }
    public int getAtendidos() { return atendidos; }
    public int getCancelados() { return cancelados; }
    public int getTotal() { return pendientes + atendidos + cancelados; }

    @Override
    public String toString() {
        return String.format("%-26s %10d %9d %10d %7d",
                nombreServicio, pendientes, atendidos, cancelados, getTotal());
    }
}
