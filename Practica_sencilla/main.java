package Practica_sencilla;

public class main {

    public static void main(String[] args) {

        tablasimbolo tabla = new tablasimbolo();

        System.out.println("=== TABLA DE SIMBOLOS ===\n");

        // int x;
        tabla.insertar("x", "int", "variable");

        // float suma(int a, int b) {
        tabla.insertar("suma", "float", "funcion");
        tabla.entrarAmbito();
        tabla.insertar("a", "int", "parametro");
        tabla.insertar("b", "int", "parametro");

        // float x;   (oculta a la x global)
        tabla.insertar("x", "float", "variable");

        tabla.mostrar();

        // x = a + b;
        simbolo x = tabla.buscar("x");
        System.out.println("Buscar x -> tipo " + x.tipo + ", nivel " + x.nivel);

        // Errores
        tabla.insertar("a", "int", "variable");   // redeclaracion
        tabla.buscar("y");                        // no declarada

        // }   fin de la funcion
        tabla.salirAmbito();
        System.out.println("\nDespues de salir de la funcion:");
        tabla.mostrar();
    }
}
