package logic;

import model.Producto;

import java.util.ArrayList;
import java.util.List;

/** Inventario de productos respaldado por un árbol AVL (clave = ID del producto). */
public class Inventario {

    private final ArbolAVL<Integer, Producto> arbol = new ArbolAVL<>();

    public void agregar(int id, String nombre, int stock) {
        Producto p = new Producto(id, nombre, stock); // valida los datos
        if (arbol.buscar(id) != null)
            throw new IllegalArgumentException("Ya existe un producto con ID " + id + ".");
        arbol.insertar(id, p);
    }

    public Producto buscar(int id) { return arbol.buscar(id); }

    public void editar(int id, String nombre, int stock) {
        Producto p = arbol.buscar(id);
        if (p == null) throw new IllegalArgumentException("No existe un producto con ID " + id + ".");
        p.setNombre(nombre);
        p.setStock(stock);
    }

    public void eliminar(int id) {
        if (!arbol.eliminar(id))
            throw new IllegalArgumentException("No existe un producto con ID " + id + ".");
    }

    public void vaciar() { arbol.vaciar(); }
    public int cantidadProductos() { return arbol.tamano(); }
    public int alturaArbol() { return arbol.altura(); }

    // ---------- Reportes (recorridos recursivos del árbol) ----------

    public List<Producto> inOrden() {
        List<Producto> lista = new ArrayList<>();
        arbol.inOrden(lista::add);
        return lista;
    }

    public List<Producto> preOrden() {
        List<Producto> lista = new ArrayList<>();
        arbol.preOrden(lista::add);
        return lista;
    }

    public List<Producto> postOrden() {
        List<Producto> lista = new ArrayList<>();
        arbol.postOrden(lista::add);
        return lista;
    }

    public int stockTotal() {
        int[] total = {0};
        arbol.inOrden(p -> total[0] += p.getStock());
        return total[0];
    }
}
