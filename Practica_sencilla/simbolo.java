package Practica_sencilla;

public class simbolo {

    String nombre;
    String tipo;
    String categoria;
    int nivel;

    public simbolo(String nombre, String tipo, String categoria, int nivel) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.categoria = categoria;
        this.nivel = nivel;
    }

    @Override
    public String toString() {
        return String.format("%-8s %-8s %-10s %d", nombre, tipo, categoria, nivel);
    }
}
