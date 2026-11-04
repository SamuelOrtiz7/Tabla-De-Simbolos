package Practica_sencilla;

import java.util.ArrayList;
import java.util.HashMap;

public class tablasimbolo {

    // Una tabla hash por cada ambito. La ultima de la lista es el ambito actual.
    private ArrayList<HashMap<String, simbolo>> ambitos = new ArrayList<>();

    public tablasimbolo() {
        entrarAmbito(); // ambito global (nivel 0)
    }

    public void entrarAmbito() {
        ambitos.add(new HashMap<>());
    }

    public void salirAmbito() {
        ambitos.remove(ambitos.size() - 1);
    }

    public void insertar(String nombre, String tipo, String categoria) {
        HashMap<String, simbolo> actual = ambitos.get(ambitos.size() - 1);

        if (actual.containsKey(nombre)) {
            System.out.println("ERROR: '" + nombre + "' ya fue declarado en este ambito");
            return;
        }
        actual.put(nombre, new simbolo(nombre, tipo, categoria, ambitos.size() - 1));
        System.out.println("Insertado: " + nombre);
    }

    public simbolo buscar(String nombre) {
        // Busca del ambito actual hacia el global
        for (int i = ambitos.size() - 1; i >= 0; i--) {
            if (ambitos.get(i).containsKey(nombre)) {
                return ambitos.get(i).get(nombre);
            }
        }
        System.out.println("ERROR: '" + nombre + "' no ha sido declarado");
        return null;
    }

    public void mostrar() {
        System.out.println("\nNOMBRE   TIPO     CATEGORIA  NIVEL");
        System.out.println("----------------------------------");
        for (int i = ambitos.size() - 1; i >= 0; i--) {
            for (simbolo s : ambitos.get(i).values()) {
                System.out.println(s);
            }
        }
        System.out.println();
    }
}