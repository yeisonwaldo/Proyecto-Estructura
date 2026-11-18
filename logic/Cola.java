package logic;

import java.util.ArrayList;
import java.util.List;

/** Cola (FIFO) genérica con nodos enlazados. */
public class Cola<T> {

    private static class Nodo<T> {
        final T dato;
        Nodo<T> siguiente;
        Nodo(T dato) { this.dato = dato; }
    }

    private Nodo<T> frente, fin;
    private int tamano;

    public void encolar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (fin == null) frente = nuevo;
        else fin.siguiente = nuevo;
        fin = nuevo;
        tamano++;
    }

    public T desencolar() {
        if (frente == null) throw new IllegalStateException("La cola está vacía.");
        T dato = frente.dato;
        frente = frente.siguiente;
        if (frente == null) fin = null;
        tamano--;
        return dato;
    }

    public T verFrente() { return frente == null ? null : frente.dato; }
    public boolean estaVacia() { return frente == null; }
    public int tamano() { return tamano; }

    /** Elementos del frente al final. */
    public List<T> aLista() {
        List<T> lista = new ArrayList<>();
        for (Nodo<T> n = frente; n != null; n = n.siguiente) lista.add(n.dato);
        return lista;
    }
}
