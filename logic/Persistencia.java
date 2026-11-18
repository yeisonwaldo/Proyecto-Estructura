package logic;

import model.Producto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Guarda y carga el inventario en un archivo de texto (formato: id;nombre;stock). */
public final class Persistencia {

    private Persistencia() { }

    public static void guardar(Inventario inventario, Path ruta) throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Producto p : inventario.inOrden()) {
            lineas.add(p.getId() + ";" + p.getNombre().replace(';', ',') + ";" + p.getStock());
        }
        Files.write(ruta, lineas, StandardCharsets.UTF_8);
    }

    /** @return cantidad de productos cargados correctamente. */
    public static int cargar(Inventario inventario, Path ruta) throws IOException {
        if (!Files.exists(ruta)) return 0;
        int cargados = 0;
        for (String linea : Files.readAllLines(ruta, StandardCharsets.UTF_8)) {
            if (linea.isBlank()) continue;
            try {
                String[] t = linea.split(";");
                inventario.agregar(Integer.parseInt(t[0].trim()), t[1].trim(), Integer.parseInt(t[2].trim()));
                cargados++;
            } catch (RuntimeException e) {
                // Línea inválida o ID duplicado: se ignora y se sigue con la siguiente.
            }
        }
        return cargados;
    }
}
