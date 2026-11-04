/**
 * Representa una entrada de la tabla de simbolos.
 * Cada simbolo relaciona un nombre (lexema) con sus atributos.
 */
public class Simbolo {

    private final String nombre;        // lexema del identificador
    private final String categoria;     // variable, parametro, funcion
    private final String tipo;          // int, float, etc.
    private final int nivel;            // nivel de ambito (0 = global)
    private final String ambito;        // nombre del ambito donde se declaro
    private final int desplazamiento;   // posicion relativa en memoria (bytes), -1 si no aplica

    public Simbolo(String nombre, String categoria, String tipo,
                   int nivel, String ambito, int desplazamiento) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.tipo = tipo;
        this.nivel = nivel;
        this.ambito = ambito;
        this.desplazamiento = desplazamiento;
    }

    public String getNombre()       { return nombre; }
    public String getCategoria()    { return categoria; }
    public String getTipo()         { return tipo; }
    public int getNivel()           { return nivel; }
    public String getAmbito()       { return ambito; }
    public int getDesplazamiento()  { return desplazamiento; }

    /** Forma corta: "x:int" */
    public String resumen() {
        return nombre + ":" + tipo;
    }

    /** Fila para imprimir en la tabla. */
    @Override
    public String toString() {
        String desp = desplazamiento < 0 ? "-" : String.valueOf(desplazamiento);
        return String.format("| %-6s | %-10s | %-20s | %-8s | %-5d | %-6s |",
                nombre, categoria, tipo, ambito, nivel, desp);
    }
}