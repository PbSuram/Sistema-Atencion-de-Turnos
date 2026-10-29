package com.oficina.turnos.estructuras;

import com.oficina.turnos.modelo.Turno;

/** Un eslabón de la cola: guarda un turno y apunta al siguiente nodo. */
public class NodoCola {
    private final Turno turno;
    private NodoCola siguiente;

    public NodoCola(Turno turno) {
        this.turno = turno;
        this.siguiente = null;
    }

    public Turno getTurno() { return turno; }
    public NodoCola getSiguiente() { return siguiente; }
    public void setSiguiente(NodoCola siguiente) { this.siguiente = siguiente; }
}
