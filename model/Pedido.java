package model;

/** Pedido de un cliente: nace en la Cola, se carga en la Pila (camión) y se entrega. */
public class Pedido {
    public enum Estado { EN_COLA, CARGADO, ENTREGADO, RECHAZADO }

    private final int id;
    private final String cliente;
    private final int productoId;
    private final int cantidad;
    private Estado estado = Estado.EN_COLA;

    public Pedido(int id, String cliente, int productoId, int cantidad) {
        this.id = id;
        this.cliente = cliente;
        this.productoId = productoId;
        this.cantidad = cantidad;
    }

    public int getId() { return id; }
    public String getCliente() { return cliente; }
    public int getProductoId() { return productoId; }
    public int getCantidad() { return cantidad; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Pedido #" + id + " (" + cliente + ", prod. " + productoId + " x" + cantidad + ")";
    }
}
