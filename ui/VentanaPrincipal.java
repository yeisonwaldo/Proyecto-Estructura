package ui;

import logic.Inventario;
import logic.Logistica;
import logic.Persistencia;
import model.Pedido;
import model.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/** Interfaz gráfica (Swing) de EcoRoute Logistics. */
public class VentanaPrincipal extends JFrame {

    private static final Path ARCHIVO = Paths.get("inventario.txt");

    /** Acción de la interfaz que puede lanzar excepciones. */
    private interface Accion { void ejecutar() throws Exception; }

    private final Inventario inventario = new Inventario();
    private final Logistica logistica = new Logistica(inventario, 8);

    // Inventario
    private final JTextField txtId = new JTextField(8);
    private final JTextField txtNombre = new JTextField(18);
    private final JTextField txtStock = new JTextField(8);
    private final DefaultTableModel modeloInventario = crearModelo("ID", "Nombre", "Stock");
    private final JTable tablaInventario = new JTable(modeloInventario);

    // Pedidos (cola)
    private final JTextField txtCliente = new JTextField(14);
    private final JTextField txtPedidoProducto = new JTextField(6);
    private final JTextField txtPedidoCantidad = new JTextField(6);
    private final DefaultTableModel modeloCola =
            crearModelo("Posición", "Pedido", "Cliente", "ID producto", "Cantidad", "Estado");

    // Camión (pila)
    private final DefaultTableModel modeloCamion =
            crearModelo("Posición", "Pedido", "Cliente", "Producto", "Cantidad");
    private final JLabel lblCamion = new JLabel();

    // Historial y reportes
    private final JTextArea areaHistorial = crearAreaTexto();
    private final JTextArea areaReporte = crearAreaTexto();

    public VentanaPrincipal() {
        super("EcoRoute Logistics - El Cerebro de Distribución");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        cargarInventarioInicial();

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("1. Inventario (AVL)", panelInventario());
        pestanas.addTab("2. Pedidos (Cola)", panelPedidos());
        pestanas.addTab("3. Camión (Pila)", panelCamion());
        pestanas.addTab("Historial (Lista)", new JScrollPane(areaHistorial));
        pestanas.addTab("Reportes", panelReportes());
        add(pestanas);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    Persistencia.guardar(inventario, ARCHIVO);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(VentanaPrincipal.this,
                            "No se pudo guardar el inventario: " + ex.getMessage());
                }
                dispose();
                System.exit(0);
            }
        });

        refrescar();
    }

    // ---------------------------------------------------------------- paneles

    private JPanel panelInventario() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulario.add(new JLabel("ID:"));
        formulario.add(txtId);
        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);
        formulario.add(new JLabel("Stock:"));
        formulario.add(txtStock);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(boton("Agregar", () -> {
            inventario.agregar(entero(txtId), txtNombre.getText(), entero(txtStock));
            limpiarFormularioInventario();
        }));
        botones.add(boton("Buscar por ID", () -> {
            Producto p = inventario.buscar(entero(txtId));
            if (p == null) throw new IllegalArgumentException("No existe un producto con ese ID.");
            txtNombre.setText(p.getNombre());
            txtStock.setText(String.valueOf(p.getStock()));
            JOptionPane.showMessageDialog(this, "Encontrado: " + p);
        }));
        botones.add(boton("Editar", () ->
                inventario.editar(entero(txtId), txtNombre.getText(), entero(txtStock))));
        botones.add(boton("Eliminar", () -> {
            int id = entero(txtId);
            int r = JOptionPane.showConfirmDialog(this, "¿Eliminar el producto " + id + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                inventario.eliminar(id);
                limpiarFormularioInventario();
            }
        }));
        botones.add(boton("Guardar archivo", () -> {
            Persistencia.guardar(inventario, ARCHIVO);
            JOptionPane.showMessageDialog(this, "Inventario guardado en " + ARCHIVO.toAbsolutePath());
        }));
        botones.add(boton("Cargar archivo", () -> {
            inventario.vaciar();
            int n = Persistencia.cargar(inventario, ARCHIVO);
            JOptionPane.showMessageDialog(this, n + " producto(s) cargado(s).");
        }));

        JPanel arriba = new JPanel(new GridLayout(2, 1));
        arriba.add(formulario);
        arriba.add(botones);

        tablaInventario.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaInventario.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                txtId.setText(String.valueOf(modeloInventario.getValueAt(fila, 0)));
                txtNombre.setText(String.valueOf(modeloInventario.getValueAt(fila, 1)));
                txtStock.setText(String.valueOf(modeloInventario.getValueAt(fila, 2)));
            }
        });

        panel.add(arriba, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaInventario), BorderLayout.CENTER);
        return panel;
    }

    private JPanel panelPedidos() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulario.add(new JLabel("Cliente:"));
        formulario.add(txtCliente);
        formulario.add(new JLabel("ID producto:"));
        formulario.add(txtPedidoProducto);
        formulario.add(new JLabel("Cantidad:"));
        formulario.add(txtPedidoCantidad);
        formulario.add(boton("Registrar pedido", () -> {
            logistica.registrarPedido(txtCliente.getText(), entero(txtPedidoProducto), entero(txtPedidoCantidad));
            txtCliente.setText("");
            txtPedidoProducto.setText("");
            txtPedidoCantidad.setText("");
        }));
        formulario.add(boton("Procesar siguiente → camión", () -> {
            Pedido p = logistica.procesarSiguiente();
            if (p.getEstado() == Pedido.Estado.RECHAZADO)
                JOptionPane.showMessageDialog(this, p + " fue rechazado por falta de stock o producto inexistente.");
        }));

        panel.add(formulario, BorderLayout.NORTH);
        panel.add(new JScrollPane(new JTable(modeloCola)), BorderLayout.CENTER);
        panel.add(new JLabel("Los pedidos se procesan en orden de llegada (FIFO): el de la posición 1 sale primero."),
                BorderLayout.SOUTH);
        return panel;
    }

    private JPanel panelCamion() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(boton("Entregar siguiente (desapilar)", () -> {
            Pedido p = logistica.entregarSiguiente();
            JOptionPane.showMessageDialog(this, p + " entregado a " + p.getCliente() + ".");
        }));
        acciones.add(boton("Entregar todo el camión", () -> {
            int n = logistica.entregarTodo();
            JOptionPane.showMessageDialog(this, n + " paquete(s) entregado(s) en orden LIFO.");
        }));
        acciones.add(boton("Invertir orden (recursivo)", logistica::invertirCamion));
        acciones.add(lblCamion);

        panel.add(acciones, BorderLayout.NORTH);
        panel.add(new JScrollPane(new JTable(modeloCamion)), BorderLayout.CENTER);
        panel.add(new JLabel("Posición 1 = cima de la pila: es el último paquete cargado y el primero en entregarse (LIFO)."),
                BorderLayout.SOUTH);
        return panel;
    }

    private JPanel panelReportes() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(boton("In-Order (por ID)", () -> mostrarReporte("Recorrido In-Order", inventario.inOrden())));
        botones.add(boton("Pre-Orden", () -> mostrarReporte("Recorrido Pre-Orden", inventario.preOrden())));
        botones.add(boton("Post-Orden", () -> mostrarReporte("Recorrido Post-Orden", inventario.postOrden())));
        botones.add(boton("Estadísticas", () -> areaReporte.setText(
                "=== Estadísticas del inventario ===\n"
                        + "Productos registrados: " + inventario.cantidadProductos() + "\n"
                        + "Stock total: " + inventario.stockTotal() + "\n"
                        + "Altura del árbol AVL: " + inventario.alturaArbol() + "\n"
                        + "Pedidos en cola: " + logistica.pedidosEnCola().size() + "\n"
                        + "Paquetes en el camión: " + logistica.paquetesEnCamion()
                        + " de " + logistica.capacidadCamion() + "\n")));

        panel.add(botones, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaReporte), BorderLayout.CENTER);
        return panel;
    }

    // ---------------------------------------------------------------- utilidades

    private void mostrarReporte(String titulo, List<Producto> productos) {
        StringBuilder sb = new StringBuilder("=== ").append(titulo).append(" ===\n");
        if (productos.isEmpty()) sb.append("(inventario vacío)\n");
        for (Producto p : productos)
            sb.append(String.format("ID %-6d %-25s stock: %d%n", p.getId(), p.getNombre(), p.getStock()));
        areaReporte.setText(sb.toString());
    }

    private JButton boton(String texto, Accion accion) {
        JButton b = new JButton(texto);
        b.addActionListener(e -> correr(accion));
        return b;
    }

    /** Ejecuta una acción manejando excepciones (Fase 5: try-catch) y refresca las vistas. */
    private void correr(Accion accion) {
        try {
            accion.ejecutar();
        } catch (NumberFormatException ex) {
            error("Verifica que los campos numéricos contengan solo números enteros.");
        } catch (Exception ex) {
            error(ex.getMessage() == null ? ex.toString() : ex.getMessage());
        }
        refrescar();
    }

    private void error(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private static int entero(JTextField campo) {
        return Integer.parseInt(campo.getText().trim());
    }

    private void limpiarFormularioInventario() {
        txtId.setText("");
        txtNombre.setText("");
        txtStock.setText("");
    }

    private void cargarInventarioInicial() {
        try {
            Persistencia.cargar(inventario, ARCHIVO);
        } catch (Exception ex) {
            error("No se pudo leer " + ARCHIVO + ": " + ex.getMessage());
        }
    }

    private void refrescar() {
        modeloInventario.setRowCount(0);
        for (Producto p : inventario.inOrden())
            modeloInventario.addRow(new Object[]{p.getId(), p.getNombre(), p.getStock()});

        modeloCola.setRowCount(0);
        int pos = 1;
        for (Pedido p : logistica.pedidosEnCola())
            modeloCola.addRow(new Object[]{pos++, "#" + p.getId(), p.getCliente(),
                    p.getProductoId(), p.getCantidad(), p.getEstado()});

        modeloCamion.setRowCount(0);
        pos = 1;
        for (Pedido p : logistica.contenidoCamion()) {
            Producto prod = inventario.buscar(p.getProductoId());
            String nombre = prod == null ? "(eliminado)" : prod.getNombre();
            modeloCamion.addRow(new Object[]{pos == 1 ? "1 (cima)" : String.valueOf(pos),
                    "#" + p.getId(), p.getCliente(), nombre, p.getCantidad()});
            pos++;
        }
        lblCamion.setText("   Carga: " + logistica.paquetesEnCamion() + " / " + logistica.capacidadCamion());

        areaHistorial.setText(String.join("\n", logistica.historial()));
        areaHistorial.setCaretPosition(areaHistorial.getDocument().getLength());
    }

    private static DefaultTableModel crearModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) { return false; }
        };
    }

    private static JTextArea crearAreaTexto() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        return area;
    }
}
