package com.oficina.turnos.estructuras;

import com.oficina.turnos.modelo.Cliente;

/** Nodo del árbol binario de búsqueda: guarda un cliente y sus dos hijos. */
public class NodoBST {
    private final Cliente cliente;
    private NodoBST izquierdo;
    private NodoBST derecho;

    public NodoBST(Cliente cliente) {
        this.cliente = cliente;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public NodoBST getIzquierdo() {
        return izquierdo;
    }

    public void setIzquierdo(NodoBST izquierdo) {
        this.izquierdo = izquierdo;
    }

    public NodoBST getDerecho() {
        return derecho;
    }

    public void setDerecho(NodoBST derecho) {
        this.derecho = derecho;
    }
}
