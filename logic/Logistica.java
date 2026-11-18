package logic;

import model.Pedido;
import model.Producto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Orquesta las cuatro estructuras:
 *  - Árbol AVL (Inventario): stock de productos.
 *  - Cola: pedidos esperando despacho (FIFO).
 *  - Pila: camión cargado (LIFO).
 *  - Lista enlazada: historial / bitácora de movimientos.
 */
public class Logistica {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final Inventario inventario;
    private final Cola<Pedido> colaDespacho = new Cola<>();
    private final Pila<Pedido> camion = new Pila<>();
    private final ListaEnlazada<String> historial = new ListaEnlazada<>();
    private final int capacidadCamion;
    private int siguienteId = 1;

    public Logistica(Inventario inventario, int capacidadCamion) {
        if (capacidadCamion <= 0) throw new IllegalArgumentException("La capacidad debe ser positiva.");
        this.inventario = inventario;
        this.capacidadCamion = capacidadCamion;
    }

    /** Fase 2: el pedido llega y entra a la COLA. */
    public Pedido registrarPedido(String cliente, int productoId, int cantidad) {
        if (cliente == null || cliente.isBlank())
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        if (cantidad <= 0)
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        if (inventario.buscar(productoId) == null)
            throw new IllegalArgumentException("No existe un producto con ID " + productoId + ".");

        Pedido pedido = new Pedido(siguienteId++, cliente.trim(), productoId, cantidad);
        colaDespacho.encolar(pedido);
        registrar(pedido + " recibido y puesto en la cola de despacho.");
        return pedido;
    }

    /**
     * Fase 3: saca el primer pedido de la COLA, descuenta el stock en el ÁRBOL
     * y lo apila en el camión (PILA). Si no hay stock, el pedido se rechaza.
     */
    public Pedido procesarSiguiente() {
        Pedido pedido = colaDespacho.verFrente();
        if (pedido == null)
            throw new IllegalStateException("No hay pedidos en la cola de despacho.");
        if (camion.tamano() >= capacidadCamion)
            throw new IllegalStateException("El camión está lleno (" + capacidadCamion
                    + " paquetes). Realiza entregas antes de cargar más.");

        colaDespacho.desencolar();
        Producto producto = inventario.buscar(pedido.getProductoId());
        if (producto == null || producto.getStock() < pedido.getCantidad()) {
            pedido.setEstado(Pedido.Estado.RECHAZADO);
            registrar(pedido + " RECHAZADO: producto inexistente o stock insuficiente.");
            return pedido;
        }
        producto.setStock(producto.getStock() - pedido.getCantidad());
        pedido.setEstado(Pedido.Estado.CARGADO);
        camion.apilar(pedido);
        registrar(pedido + " aprobado: el stock de '" + producto.getNombre() + "' queda en "
                + producto.getStock() + "; cargado en el camión.");
        return pedido;
    }

    /** Entrega el último paquete cargado (LIFO). */
    public Pedido entregarSiguiente() {
        if (camion.estaVacia()) throw new IllegalStateException("El camión está vacío.");
        Pedido pedido = camion.desapilar();
        pedido.setEstado(Pedido.Estado.ENTREGADO);
        registrar(pedido + " ENTREGADO a " + pedido.getCliente() + ".");
        return pedido;
    }

    /** Entrega todo el camión, de la cima a la base. Devuelve cuántos paquetes entregó. */
    public int entregarTodo() {
        if (camion.estaVacia()) throw new IllegalStateException("El camión está vacío.");
        int entregados = 0;
        while (!camion.estaVacia()) {
            entregarSiguiente();
            entregados++;
        }
        return entregados;
    }

    /** Invierte el orden del camión (recursivo, ver Pila.invertir). */
    public void invertirCamion() {
        if (camion.estaVacia()) throw new IllegalStateException("El camión está vacío.");
        camion.invertir();
        registrar("Se invirtió el orden de carga del camión.");
    }

    private void registrar(String mensaje) {
        historial.agregar("[" + LocalDateTime.now().format(FORMATO) + "] " + mensaje);
    }

    // ---------- Consultas para la interfaz ----------

    public List<Pedido> pedidosEnCola() { return colaDespacho.aLista(); }
    public List<Pedido> contenidoCamion() { return camion.aLista(); }
    public List<String> historial() { return historial.aLista(); }
    public int capacidadCamion() { return capacidadCamion; }
    public int paquetesEnCamion() { return camion.tamano(); }
}
