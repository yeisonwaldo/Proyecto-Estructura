package logic;

import java.util.ArrayList;
import java.util.List;

/** Lista simplemente enlazada genérica (con puntero al final para agregar en O(1)). */
public class ListaEnlazada<T> {

    private static class Nodo<T> {
        final T dato;
        Nodo<T> siguiente;
        Nodo(T dato) { this.dato = dato; }
    }

    private Nodo<T> cabeza, cola;
    private int tamano;

    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) cabeza = nuevo;
        else cola.siguiente = nuevo;
        cola = nuevo;
        tamano++;
    }

    public T obtener(int indice) {
        if (indice < 0 || indice >= tamano)
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        Nodo<T> n = cabeza;
        for (int i = 0; i < indice; i++) n = n.siguiente;
        return n.dato;
    }

    public int tamano() { return tamano; }
    public boolean estaVacia() { return cabeza == null; }

    public List<T> aLista() {
        List<T> lista = new ArrayList<>();
        for (Nodo<T> n = cabeza; n != null; n = n.siguiente) lista.add(n.dato);
        return lista;
    }
}
