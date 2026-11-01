package com.oficina.turnos.estructuras;

import com.oficina.turnos.modelo.Cliente;

import java.util.ArrayList;
import java.util.List;

/**
 * Árbol binario de búsqueda propio para los clientes, con el código como clave.
 * Regla: códigos menores van a la izquierda y mayores a la derecha.
 * Inserción, búsqueda e inorden son recursivos.
 * Los clientes inactivos NO se eliminan: solo cambia su estado.
 */
public class ArbolClientes {
    private NodoBST raiz;
    private int cantidad;

    /**
     * Inserta un cliente. Devuelve false si el código ya existe
     * (el BST no admite claves repetidas).
     */
    public boolean insertar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }
        if (buscar(cliente.getCodigo()) != null) {
            return false;
        }
        raiz = insertarRecursivo(raiz, cliente);
        cantidad++;
        return true;
    }

    private NodoBST insertarRecursivo(NodoBST actual, Cliente cliente) {
        if (actual == null) {
            return new NodoBST(cliente);              // caso base: lugar vacío encontrado
        }
        if (cliente.getCodigo() < actual.getCliente().getCodigo()) {
            actual.setIzquierdo(insertarRecursivo(actual.getIzquierdo(), cliente));
        } else {
            actual.setDerecho(insertarRecursivo(actual.getDerecho(), cliente));
        }
        return actual;
    }

    /** Busca un cliente por código. Devuelve null si no existe. */
    public Cliente buscar(int codigo) {
        NodoBST nodo = buscarRecursivo(raiz, codigo);
        return nodo == null ? null : nodo.getCliente();
    }

    private NodoBST buscarRecursivo(NodoBST actual, int codigo) {
        if (actual == null) {
            return null;                              // caso base: no está
        }
        int codigoActual = actual.getCliente().getCodigo();
        if (codigo == codigoActual) {
            return actual;                            // caso base: encontrado
        }
        if (codigo < codigoActual) {
            return buscarRecursivo(actual.getIzquierdo(), codigo);
        }
        return buscarRecursivo(actual.getDerecho(), codigo);
    }

    /** Recorrido inorden (izquierdo, nodo, derecho): clientes ordenados por código. */
    public List<Cliente> recorrerInorden() {
        List<Cliente> resultado = new ArrayList<>();
        inordenRecursivo(raiz, resultado);
        return resultado;
    }

    private void inordenRecursivo(NodoBST actual, List<Cliente> resultado) {
        if (actual == null) {
            return;
        }
        inordenRecursivo(actual.getIzquierdo(), resultado);
        resultado.add(actual.getCliente());
        inordenRecursivo(actual.getDerecho(), resultado);
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public int getCantidad() {
        return cantidad;
    }

    /** Vacía el árbol (se usa antes de reconstruirlo desde SQLite). */
    public void limpiar() {
        raiz = null;
        cantidad = 0;
    }
}
