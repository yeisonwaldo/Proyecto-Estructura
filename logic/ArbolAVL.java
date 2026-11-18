package logic;

import java.util.function.Consumer;

/**
 * Árbol binario de búsqueda auto-balanceado (AVL), genérico en clave y valor.
 * Recorridos, búsqueda, inserción y eliminación son RECURSIVOS.
 */
public class ArbolAVL<K extends Comparable<K>, V> {

    private class Nodo {
        K clave;
        V valor;
        Nodo izq, der;
        int altura = 1;

        Nodo(K clave, V valor) { this.clave = clave; this.valor = valor; }
    }

    private Nodo raiz;
    private int tamano;
    private boolean eliminado; // bandera auxiliar para eliminar()

    // ---------- API pública ----------

    public void insertar(K clave, V valor) { raiz = insertar(raiz, clave, valor); }

    public V buscar(K clave) { return buscar(raiz, clave); }

    public boolean eliminar(K clave) {
        eliminado = false;
        raiz = eliminar(raiz, clave);
        if (eliminado) tamano--;
        return eliminado;
    }

    public void vaciar() { raiz = null; tamano = 0; }
    public int tamano() { return tamano; }
    public boolean estaVacio() { return raiz == null; }
    public int altura() { return altura(raiz); }

    public void inOrden(Consumer<V> accion)   { inOrden(raiz, accion); }
    public void preOrden(Consumer<V> accion)  { preOrden(raiz, accion); }
    public void postOrden(Consumer<V> accion) { postOrden(raiz, accion); }

    // ---------- Recursión: recorridos ----------

    private void inOrden(Nodo n, Consumer<V> accion) {
        if (n == null) return;
        inOrden(n.izq, accion);
        accion.accept(n.valor);
        inOrden(n.der, accion);
    }

    private void preOrden(Nodo n, Consumer<V> accion) {
        if (n == null) return;
        accion.accept(n.valor);
        preOrden(n.izq, accion);
        preOrden(n.der, accion);
    }

    private void postOrden(Nodo n, Consumer<V> accion) {
        if (n == null) return;
        postOrden(n.izq, accion);
        postOrden(n.der, accion);
        accion.accept(n.valor);
    }

    // ---------- Recursión: búsqueda / inserción / eliminación ----------

    private V buscar(Nodo n, K clave) {
        if (n == null) return null;
        int c = clave.compareTo(n.clave);
        if (c < 0) return buscar(n.izq, clave);
        if (c > 0) return buscar(n.der, clave);
        return n.valor;
    }

    private Nodo insertar(Nodo n, K clave, V valor) {
        if (n == null) {
            tamano++;
            return new Nodo(clave, valor);
        }
        int c = clave.compareTo(n.clave);
        if (c < 0) n.izq = insertar(n.izq, clave, valor);
        else if (c > 0) n.der = insertar(n.der, clave, valor);
        else throw new IllegalArgumentException("Ya existe un elemento con la clave " + clave + ".");
        return balancear(n);
    }

    private Nodo eliminar(Nodo n, K clave) {
        if (n == null) return null;
        int c = clave.compareTo(n.clave);
        if (c < 0) n.izq = eliminar(n.izq, clave);
        else if (c > 0) n.der = eliminar(n.der, clave);
        else {
            eliminado = true;
            if (n.izq == null) return n.der;
            if (n.der == null) return n.izq;
            Nodo sucesor = n.der;                 // menor del subárbol derecho
            while (sucesor.izq != null) sucesor = sucesor.izq;
            n.clave = sucesor.clave;
            n.valor = sucesor.valor;
            n.der = eliminar(n.der, sucesor.clave);
        }
        return balancear(n);
    }

    // ---------- Balanceo AVL ----------

    private int altura(Nodo n) { return n == null ? 0 : n.altura; }

    private void actualizarAltura(Nodo n) {
        n.altura = 1 + Math.max(altura(n.izq), altura(n.der));
    }

    private int factorBalance(Nodo n) {
        return n == null ? 0 : altura(n.izq) - altura(n.der);
    }

    private Nodo rotarDerecha(Nodo y) {
        Nodo x = y.izq;
        y.izq = x.der;
        x.der = y;
        actualizarAltura(y);
        actualizarAltura(x);
        return x;
    }

    private Nodo rotarIzquierda(Nodo x) {
        Nodo y = x.der;
        x.der = y.izq;
        y.izq = x;
        actualizarAltura(x);
        actualizarAltura(y);
        return y;
    }

    private Nodo balancear(Nodo n) {
        actualizarAltura(n);
        int fb = factorBalance(n);
        if (fb > 1) {
            if (factorBalance(n.izq) < 0) n.izq = rotarIzquierda(n.izq);
            return rotarDerecha(n);
        }
        if (fb < -1) {
            if (factorBalance(n.der) > 0) n.der = rotarDerecha(n.der);
            return rotarIzquierda(n);
        }
        return n;
    }
}
