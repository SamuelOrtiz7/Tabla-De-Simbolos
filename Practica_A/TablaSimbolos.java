package Practica_A;

import java.util.ArrayList;
import java.util.List;

/**
 * Logica de la tabla de simbolos: una lista plana (un solo ambito).
 * No usa nada de la interfaz grafica.
 */
public class TablaSimbolos {

    private final ArrayList<Simbolo> tabla = new ArrayList<>();

    /** Indica si ya existe una variable con ese nombre (busqueda lineal). */
    public boolean existe(String nombre) {
        for (Simbolo s : tabla) {
            if (s.nombre.equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    /** Agrega la variable. Devuelve false si ya existia. */
    public boolean agregar(String nombre, String tipo) {
        if (existe(nombre)) {
            return false;
        }
        tabla.add(new Simbolo(nombre, tipo));
        return true;
    }

    public List<Simbolo> simbolos() {
        return tabla;
    }
}
