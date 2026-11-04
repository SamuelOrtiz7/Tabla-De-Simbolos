package Practica_sencilla;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tabla de simbolos con una tabla hash por cada ambito.
 * El ultimo elemento de la lista es el ambito actual; el primero, el global.
 * No imprime nada: devuelve resultados para que la interfaz los muestre.
 */
public class TablaSimbolo {

    private final List<Map<String, Simbolo>> ambitos = new ArrayList<>();

    public TablaSimbolo() {
        entrarAmbito(); // ambito global (nivel 0)
    }

    public void entrarAmbito() {
        ambitos.add(new LinkedHashMap<>());
    }

    public void salirAmbito() {
        ambitos.remove(ambitos.size() - 1);
    }

    public int nivelActual() {
        return ambitos.size() - 1;
    }

    /** Inserta en el ambito actual. Devuelve null si todo va bien, o el mensaje de error. */
    public String insertar(String nombre, String tipo, String categoria) {
        Map<String, Simbolo> actual = ambitos.get(nivelActual());
        if (actual.containsKey(nombre)) {
            return "'" + nombre + "' ya fue declarado en este ámbito (redeclaración)";
        }
        actual.put(nombre, new Simbolo(nombre, tipo, categoria, nivelActual()));
        return null;
    }

    /** Busca del ambito actual hacia el global. Devuelve null si no existe. */
    public Simbolo buscar(String nombre) {
        for (int i = ambitos.size() - 1; i >= 0; i--) {
            Simbolo s = ambitos.get(i).get(nombre);
            if (s != null) return s;
        }
        return null;
    }

    /** Todos los simbolos, del ambito mas interno al global. */
    public List<Simbolo> simbolos() {
        List<Simbolo> lista = new ArrayList<>();
        for (int i = ambitos.size() - 1; i >= 0; i--) {
            lista.addAll(ambitos.get(i).values());
        }
        return lista;
    }
}
