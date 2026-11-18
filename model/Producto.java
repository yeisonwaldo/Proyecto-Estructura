package model;

/** Producto del inventario (cada nodo del árbol AVL guarda uno). */
public class Producto {
    private final int id;
    private String nombre;
    private int stock;

    public Producto(int id, String nombre, int stock) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser un entero positivo.");
        this.id = id;
        setNombre(nombre);
        setStock(stock);
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getStock() { return stock; }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        this.nombre = nombre.trim();
    }

    public void setStock(int stock) {
        if (stock < 0) throw new IllegalArgumentException("El stock no puede ser negativo.");
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + nombre + " (stock: " + stock + ")";
    }
}
