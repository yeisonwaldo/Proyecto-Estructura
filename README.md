# EcoRoute Logistics

Compilar y ejecutar (Java 11+), desde esta carpeta:

    javac -encoding UTF-8 -d out src/model/*.java src/logic/*.java src/ui/*.java
    java -cp out ui.Main

El inventario se guarda en `inventario.txt` (formato `id;nombre;stock`) al cerrar la ventana y se carga al abrir.
El diagrama UML está en `src/diagrama_clases.puml` (PlantUML).
