package com.oficina.turnos.modelo;

/**
 * Representa a un cliente de la oficina. El código es la clave del BST,
 * por eso es final (inmodificable).
 */
public class Cliente {
    private final int codigo;
    private String nombre;
    private String telefono;
    private EstadoGeneral estado;

    public Cliente(int codigo, String nombre, String telefono, EstadoGeneral estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.telefono = telefono;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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
        return codigo + " | " + nombre + " | " + telefono + " | " + estado;
    }
}
