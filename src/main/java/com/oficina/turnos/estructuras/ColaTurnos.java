package com.oficina.turnos.estructuras;

import com.oficina.turnos.modelo.Turno;

import java.util.ArrayList;
import java.util.List;

/**
 * Cola FIFO de turnos pendientes, hecha con nodos propios (sin LinkedList
 * ni ArrayDeque). "frente" es el próximo a atender y "fin" el último en llegar.
 */
public class ColaTurnos {
    private NodoCola frente;
    private NodoCola fin;
    private int tamanio;

    public ColaTurnos() {
        frente = null;
        fin = null;
        tamanio = 0;
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int getTamanio() {
        return tamanio;
    }

    /** Agrega un turno al final de la cola. */
    public void encolar(Turno turno) {
        NodoCola nuevo = new NodoCola(turno);
        if (estaVacia()) {
            frente = nuevo;           // es el único nodo: es frente y fin a la vez
        } else {
            fin.setSiguiente(nuevo);  // el último actual apunta al nuevo
        }
        fin = nuevo;
        tamanio++;
    }

    /** Devuelve el primer turno sin retirarlo, o null si la cola está vacía. */
    public Turno verFrente() {
        return estaVacia() ? null : frente.getTurno();
    }

    /** Retira y devuelve el primer turno (FIFO), o null si la cola está vacía. */
    public Turno desencolar() {
        if (estaVacia()) {
            return null;
        }
        Turno turno = frente.getTurno();
        frente = frente.getSiguiente();
        if (frente == null) {
            fin = null;               // la cola quedó vacía
        }
        tamanio--;
        return turno;
    }

    /** Busca un turno por número recorriendo los nodos. */
    public Turno buscar(int numero) {
        NodoCola actual = frente;
        while (actual != null) {
            if (actual.getTurno().getNumero() == numero) {
                return actual.getTurno();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    /** Indica si el cliente ya tiene un turno en la cola (pendiente). */
    public boolean tieneTurnoDeCliente(int codigoCliente) {
        NodoCola actual = frente;
        while (actual != null) {
            if (actual.getTurno().getCodigoCliente() == codigoCliente) {
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    /**
     * Retira de la cola el turno con ese número, esté donde esté,
     * reenlazando el nodo anterior con el siguiente para no alterar
     * el orden de los demás. Devuelve el turno retirado o null.
     */
    public Turno cancelar(int numero) {
        NodoCola anterior = null;
        NodoCola actual = frente;

        while (actual != null && actual.getTurno().getNumero() != numero) {
            anterior = actual;
            actual = actual.getSiguiente();
        }
        if (actual == null) {
            return null;              // no estaba en la cola
        }

        if (anterior == null) {
            frente = actual.getSiguiente();               // era el primero
        } else {
            anterior.setSiguiente(actual.getSiguiente()); // "saltamos" el nodo
        }
        if (actual == fin) {
            fin = anterior;                               // era el último
        }
        tamanio--;
        return actual.getTurno();
    }

    /** Recorre la cola y devuelve los turnos en orden de atención. */
    public List<Turno> recorrer() {
        List<Turno> turnos = new ArrayList<>();
        NodoCola actual = frente;
        while (actual != null) {
            turnos.add(actual.getTurno());
            actual = actual.getSiguiente();
        }
        return turnos;
    }

    /** Muestra la cola en consola, del frente al final. */
    public void mostrar() {
        if (estaVacia()) {
            System.out.println("No hay turnos pendientes.");
            return;
        }
        NodoCola actual = frente;
        int posicion = 1;
        while (actual != null) {
            System.out.println(posicion + ". " + actual.getTurno());
            actual = actual.getSiguiente();
            posicion++;
        }
    }
}
