package logic;

import java.util.ArrayList;
import java.util.List;

/** Pila (LIFO) genérica con nodos enlazados. */
public class Pila<T> {

    private static class Nodo<T> {
        final T dato;
        Nodo<T> siguiente;
        Nodo(T dato, Nodo<T> siguiente) { this.dato = dato; this.siguiente = siguiente; }
    }

    private Nodo<T> cima;
    private int tamano;

    public void apilar(T dato) {
        cima = new Nodo<>(dato, cima);
        tamano++;
    }

    public T desapilar() {
        if (cima == null) throw new IllegalStateException("La pila está vacía.");
        T dato = cima.dato;
        cima = cima.siguiente;
        tamano--;
        return dato;
    }

    public T verCima() { return cima == null ? null : cima.dato; }
    public boolean estaVacia() { return cima == null; }
    public int tamano() { return tamano; }

    /** Elementos de la cima hacia la base. */
    public List<T> aLista() {
        List<T> lista = new ArrayList<>();
        for (Nodo<T> n = cima; n != null; n = n.siguiente) lista.add(n.dato);
        return lista;
    }

    /** Invierte la pila con RECURSIÓN, usando solo operaciones de pila. */
    public void invertir() {
        if (estaVacia()) return;
        T dato = desapilar();
        invertir();
        insertarEnLaBase(dato);
    }

    private void insertarEnLaBase(T dato) {
        if (estaVacia()) {
            apilar(dato);
            return;
        }
        T temp = desapilar();
        insertarEnLaBase(dato);
        apilar(temp);
    }
}
