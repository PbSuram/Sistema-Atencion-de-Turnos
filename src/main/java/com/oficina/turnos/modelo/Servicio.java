package com.oficina.turnos.modelo;

/**
 * Representa un servicio que la oficina puede atender (por ejemplo,
 * "Información" o "Entrega de documentos").
 */
public class Servicio {
    private final int codigo;
    private String nombre;
    private EstadoGeneral estado;

    public Servicio(int codigo, String nombre, EstadoGeneral estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.estado = estado;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public EstadoGeneral getEstado() {
        return estado;
    }

    public void setEstado(EstadoGeneral estado) {
        this.estado = estado;
    }

    public boolean estaActivo() {
        return estado == EstadoGeneral.ACTIVO;
    }

    @Override
    public String toString() {
        return codigo + " | " + nombre + " | " + estado;
    }
}
