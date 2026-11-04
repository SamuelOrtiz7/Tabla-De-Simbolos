package Practica;

import java.util.ArrayList;
import java.util.List;

/**
 * Logica de la simulacion: los programas que se analizan y la operacion
 * que hace el compilador sobre la tabla de simbolos en cada linea.
 * No usa nada de la interfaz grafica.
 */
public class Programas {

    /** Resultado de un paso: el texto que explica lo ocurrido y si fue un error. */
    public static class Resultado {
        public final String texto;
        public final boolean error;

        Resultado(String texto, boolean error) {
            this.texto = texto;
            this.error = error;
        }
    }

    /** Operacion que el compilador realiza sobre la tabla. */
    public interface Accion {
        Resultado ejecutar(TablaSimbolo t);
    }

    /** Una linea del programa fuente junto con su operacion. */
    public static class Paso {
        public final String linea;
        public final Accion accion;

        Paso(String linea, Accion accion) {
            this.linea = linea;
            this.accion = accion;
        }
    }

    private static Resultado ok(String texto) { return new Resultado(texto, false); }
    private static Resultado error(String texto) { return new Resultado(texto, true); }

    private static Accion insertar(String nombre, String tipo, String categoria) {
        return t -> {
            String err = t.insertar(nombre, tipo, categoria);
            if (err != null) return error("ERROR: " + err);
            return ok("insertar(" + nombre + "): se agrega " + nombre + " (" + tipo + ", " + categoria
                    + ") al nivel " + t.nivelActual() + ".");
        };
    }

    private static Accion buscar(String nombre) {
        return t -> {
            Simbolo s = t.buscar(nombre);
            if (s == null) return error("ERROR: '" + nombre + "' no ha sido declarado");
            return ok("buscar(" + nombre + "): se encuentra en el nivel " + s.nivel + " con tipo " + s.tipo + ".");
        };
    }

    private static Accion combinar(Accion... acciones) {
        return t -> {
            StringBuilder sb = new StringBuilder();
            boolean hayError = false;
            for (Accion a : acciones) {
                Resultado r = a.ejecutar(t);
                if (sb.length() > 0) sb.append("<br>");
                sb.append(r.texto);
                hayError |= r.error;
            }
            return new Resultado(sb.toString(), hayError);
        };
    }

    private static final Accion ENTRAR = t -> {
        t.entrarAmbito();
        return ok("entrarAmbito(): se crea una tabla nueva, ahora el nivel actual es " + t.nivelActual() + ".");
    };

    private static final Accion SALIR = t -> {
        int nivel = t.nivelActual();
        t.salirAmbito();
        return ok("salirAmbito(): se elimina la tabla del nivel " + nivel + " y sus símbolos desaparecen.");
    };

    public static List<Paso> correcto() {
        List<Paso> p = new ArrayList<>();
        p.add(new Paso("int x;", insertar("x", "int", "variable")));
        p.add(new Paso("float suma(int a, int b) {", combinar(
                insertar("suma", "float", "función"), ENTRAR,
                insertar("a", "int", "parámetro"), insertar("b", "int", "parámetro"))));
        p.add(new Paso("    float x;", combinar(insertar("x", "float", "variable"),
                t -> ok("Esta x oculta a la x global (nivel 0)."))));
        p.add(new Paso("    x = a + b;", combinar(buscar("x"), buscar("a"), buscar("b"))));
        p.add(new Paso("    {", ENTRAR));
        p.add(new Paso("        int i;", insertar("i", "int", "variable")));
        p.add(new Paso("        i = 0;", buscar("i")));
        p.add(new Paso("    }", SALIR));
        p.add(new Paso("    return x;", buscar("x")));
        p.add(new Paso("}", SALIR));
        return p;
    }

    public static List<Paso> conErrores() {
        List<Paso> p = new ArrayList<>();
        p.add(new Paso("int x;", insertar("x", "int", "variable")));
        p.add(new Paso("float suma(int a, int b) {", combinar(
                insertar("suma", "float", "función"), ENTRAR,
                insertar("a", "int", "parámetro"), insertar("b", "int", "parámetro"))));
        p.add(new Paso("    int a;          // error", insertar("a", "int", "variable")));
        p.add(new Paso("    {", ENTRAR));
        p.add(new Paso("        int i;", insertar("i", "int", "variable")));
        p.add(new Paso("    }", SALIR));
        p.add(new Paso("    i = 5;          // error", buscar("i")));
        p.add(new Paso("    return x;", buscar("x")));
        p.add(new Paso("}", SALIR));
        return p;
    }
}
