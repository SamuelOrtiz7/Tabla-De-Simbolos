import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tabla de simbolos implementada como una PILA DE TABLAS HASH,
 * una tabla por cada ambito (global, funcion, bloque).
 *
 * Operaciones:
 *  - entrarAmbito(): abre un nuevo nivel
 *  - salirAmbito():  elimina el nivel actual y sus simbolos
 *  - insertar():     agrega un simbolo al nivel actual (detecta redeclaracion)
 *  - buscar():       busca del nivel mas interno hacia el global
 */
public class TablaSimbolo {

    /** Cada elemento de la pila es la tabla hash de un ambito (tope = mas interno). */
    private final Deque<Map<String, Simbolo>> ambitos = new ArrayDeque<>();

    /** Nombre de cada ambito, en el mismo orden que la pila. */
    private final Deque<String> nombresAmbito = new ArrayDeque<>();

    /** Siguiente desplazamiento libre en cada ambito. */
    private final Deque<Integer> desplazamientos = new ArrayDeque<>();

    /** Niveles revisados en la ultima busqueda (para mostrar el recorrido). */
    private final List<String> ultimoRecorrido = new ArrayList<>();

    public TablaSimbolo() {
        entrarAmbito("global");
    }

    // ------------------------------------------------------------------
    // Manejo de ambitos
    // ------------------------------------------------------------------

    public void entrarAmbito(String nombre) {
        ambitos.push(new LinkedHashMap<>());
        nombresAmbito.push(nombre);
        desplazamientos.push(0);
    }

    /** Elimina el ambito actual y devuelve los simbolos que se borraron. */
    public List<Simbolo> salirAmbito() {
        if (ambitos.size() == 1) {
            throw new IllegalStateException("No se puede salir del ambito global");
        }
        nombresAmbito.pop();
        desplazamientos.pop();
        return new ArrayList<>(ambitos.pop().values());
    }

    public int nivelActual() {
        return ambitos.size() - 1;
    }

    public String ambitoActual() {
        return nombresAmbito.peek();
    }

    // ------------------------------------------------------------------
    // Operaciones basicas
    // ------------------------------------------------------------------

    /**
     * Inserta un simbolo en el ambito actual.
     * Antes de insertar busca SOLO en el nivel actual para detectar redeclaraciones.
     * @return el simbolo insertado, o null si ya existia (redeclaracion)
     */
    public Simbolo insertar(String nombre, String categoria, String tipo, int tamano) {
        Map<String, Simbolo> actual = ambitos.peek();
        if (actual.containsKey(nombre)) {
            return null;
        }

        int desp = -1;
        if (tamano > 0) {
            desp = desplazamientos.pop();
            desplazamientos.push(desp + tamano);
        }

        Simbolo s = new Simbolo(nombre, categoria, tipo, nivelActual(), nombresAmbito.peek(), desp);
        actual.put(nombre, s);
        return s;
    }

    /**
     * Busca un nombre desde el ambito mas interno hacia el global.
     * Guarda el recorrido para poder mostrar en que niveles busco.
     * @return el simbolo encontrado o null si no esta declarado
     */
    public Simbolo buscar(String nombre) {
        ultimoRecorrido.clear();
        int nivel = nivelActual();
        var itNombres = nombresAmbito.iterator();

        for (Map<String, Simbolo> tabla : ambitos) {   // ArrayDeque recorre del tope a la base
            String amb = itNombres.next();
            if (tabla.containsKey(nombre)) {
                ultimoRecorrido.add("nivel " + nivel + " (" + amb + "): ENCONTRADO");
                return tabla.get(nombre);
            }
            ultimoRecorrido.add("nivel " + nivel + " (" + amb + "): no esta");
            nivel--;
        }
        return null;
    }

    public List<String> getUltimoRecorrido() {
        return ultimoRecorrido;
    }

    // ------------------------------------------------------------------
    // Consulta del contenido (para mostrarlo en pantalla)
    // ------------------------------------------------------------------

    /** Nombres de los ambitos, del mas interno al global. */
    public List<String> getNombresAmbitos() {
        return new ArrayList<>(nombresAmbito);
    }

    /** Simbolos de cada ambito, del mas interno al global. */
    public List<List<Simbolo>> getSimbolosPorAmbito() {
        List<List<Simbolo>> lista = new ArrayList<>();
        for (Map<String, Simbolo> tabla : ambitos) {
            lista.add(new ArrayList<>(tabla.values()));
        }
        return lista;
    }
}