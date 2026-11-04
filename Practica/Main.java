import java.util.List;
import java.util.Scanner;

/**
 * Practica: Tabla de simbolos
 * Materia: Automatas y Compiladores
 *
 * Simula, paso a paso, lo que hace un compilador con la tabla de simbolos
 * mientras analiza un programa pequeno. En cada paso muestra:
 *   - la linea de codigo que se esta analizando (resaltada)
 *   - la operacion que hace el compilador sobre la tabla
 *   - una explicacion en palabras
 *   - la pila de ambitos con su contenido
 *
 * Opciones al ejecutar:
 *   java -cp out Main              -> modo presentacion (pausa con ENTER)
 *   java -cp out Main --auto       -> sin pausas
 *   java -cp out Main --sin-color  -> sin colores (si tu terminal no los soporta)
 */
public class Main {

    // ---------------- Colores ANSI ----------------
    private static boolean color = true;
    private static String RESET, NEGRITA, ROJO, VERDE, AMARILLO, AZUL, MAGENTA, CIAN, GRIS;

    private static boolean pausas = true;
    private static final Scanner teclado = new Scanner(System.in);

    private static final int TAM_INT = 4;
    private static final int TAM_FLOAT = 4;

    // ---------------- Programas a analizar ----------------
    private static final String[] PROGRAMA = {
        "int x;",
        "float suma(int a, int b) {",
        "    float x;",
        "    x = a + b;",
        "    {",
        "        int i;",
        "        i = 0;",
        "    }",
        "    return x;",
        "}"
    };

    private static final String[] PROGRAMA_CON_ERRORES = {
        "int x;",
        "float suma(int a, int b) {",
        "    int a;          // error",
        "    {",
        "        int i;",
        "    }",
        "    i = 5;          // error",
        "    return x;",
        "}"
    };

    private static int pasoActual = 0;
    private static int totalPasos = 0;

    public static void main(String[] args) {
        for (String a : args) {
            if (a.equals("--auto")) pausas = false;
            if (a.equals("--sin-color")) color = false;
        }
        configurarColores();

        portada();
        casoPractico();
        casoConErrores();
        cierre();
    }

    // ==================================================================
    // PARTE 1: programa correcto
    // ==================================================================

    private static void casoPractico() {
        TablaSimbolo tabla = new TablaSimbolo();
        pasoActual = 0;
        totalPasos = 8;
        seccion("PARTE 1: ANALISIS DE UN PROGRAMA CORRECTO");

        // Linea 1
        inicioPaso(tabla, PROGRAMA, 1);
        insertar(tabla, "x", "variable", "int", TAM_INT);
        explicar("Es una declaracion en el ambito global, asi que se inserta en el nivel 0.");
        finPaso(tabla);

        // Linea 2
        inicioPaso(tabla, PROGRAMA, 2);
        insertar(tabla, "suma", "funcion", "(int, int) -> float", 0);
        entrar(tabla, "suma");
        insertar(tabla, "a", "parametro", "int", TAM_INT);
        insertar(tabla, "b", "parametro", "int", TAM_INT);
        explicar("El nombre de la funcion se guarda en el ambito global.",
                 "Al abrir la llave se crea un ambito NUEVO (nivel 1) para la funcion,",
                 "y ahi se guardan sus parametros a y b.");
        finPaso(tabla);

        // Linea 3
        inicioPaso(tabla, PROGRAMA, 3);
        insertar(tabla, "x", "variable", "float", TAM_FLOAT);
        explicar("Ya existe una x global, pero esta x se declara en OTRO ambito,",
                 "asi que no es un error. A partir de aqui la x local (float)",
                 "OCULTA a la x global (int).");
        finPaso(tabla);

        // Linea 4
        inicioPaso(tabla, PROGRAMA, 4);
        Simbolo x = buscar(tabla, "x");
        Simbolo a = buscar(tabla, "a");
        Simbolo b = buscar(tabla, "b");
        verificar(x != null && a != null && b != null
                        && x.getTipo().equals("float"),
                "int + int da un int, y un int SI se puede guardar en un float.");
        explicar("Para usar una variable, primero hay que buscarla.",
                 "La busqueda empieza en el nivel actual y sale hacia afuera:",
                 "por eso encuentra la x de la funcion (float) y no la global.");
        finPaso(tabla);

        // Lineas 5-6
        inicioPaso(tabla, PROGRAMA, 5, 6);
        entrar(tabla, "bloque");
        insertar(tabla, "i", "variable", "int", TAM_INT);
        explicar("La llave abre un bloque, y cada bloque es un ambito nuevo (nivel 2).",
                 "La variable i solo existe dentro de este bloque.");
        finPaso(tabla);

        // Linea 7
        inicioPaso(tabla, PROGRAMA, 7);
        buscar(tabla, "i");
        explicar("La i se encuentra en el nivel actual (2). Asi se ve la tabla completa",
                 "en este momento: hay DOS x en niveles distintos.");
        imprimirTabla(tabla);
        finPaso(tabla);

        // Linea 8
        inicioPaso(tabla, PROGRAMA, 8);
        salir(tabla);
        explicar("Al cerrar la llave se elimina el nivel 2 completo.",
                 "La variable i deja de existir.");
        finPaso(tabla);

        // Lineas 9-10
        inicioPaso(tabla, PROGRAMA, 9, 10);
        Simbolo xr = buscar(tabla, "x");
        Simbolo f = buscar(tabla, "suma");
        verificar(xr != null && f != null && f.getTipo().endsWith(xr.getTipo()),
                "x es float y la funcion suma devuelve float: el return es correcto.");
        salir(tabla);
        explicar("Al terminar la funcion se elimina el nivel 1 (a, b y la x local).",
                 "Solo queda el ambito global.");
        finPaso(tabla);
    }

    // ==================================================================
    // PARTE 2: programa con errores
    // ==================================================================

    private static void casoConErrores() {
        TablaSimbolo tabla = new TablaSimbolo();
        pasoActual = 0;
        totalPasos = 4;
        seccion("PARTE 2: DETECCION DE ERRORES");

        // Lineas 1-2 (preparacion)
        inicioPaso(tabla, PROGRAMA_CON_ERRORES, 1, 2);
        insertar(tabla, "x", "variable", "int", TAM_INT);
        insertar(tabla, "suma", "funcion", "(int, int) -> float", 0);
        entrar(tabla, "suma");
        insertar(tabla, "a", "parametro", "int", TAM_INT);
        insertar(tabla, "b", "parametro", "int", TAM_INT);
        explicar("Igual que antes: se registran x, suma y los parametros a y b.");
        finPaso(tabla);

        // Linea 3: redeclaracion
        inicioPaso(tabla, PROGRAMA_CON_ERRORES, 3);
        insertar(tabla, "a", "variable", "int", TAM_INT);
        explicar("Antes de insertar, el compilador busca 'a' SOLO en el nivel actual.",
                 "Como a ya es un parametro de suma (mismo nivel), es una REDECLARACION.");
        finPaso(tabla);

        // Lineas 4-6
        inicioPaso(tabla, PROGRAMA_CON_ERRORES, 4, 5, 6);
        entrar(tabla, "bloque");
        insertar(tabla, "i", "variable", "int", TAM_INT);
        salir(tabla);
        explicar("Se abre el bloque, se declara i y al cerrar la llave se elimina.");
        finPaso(tabla);

        // Linea 7: no declarada
        inicioPaso(tabla, PROGRAMA_CON_ERRORES, 7);
        buscar(tabla, "i");
        explicar("La busqueda revisa todos los niveles y no encuentra i,",
                 "porque se borro al salir del bloque: IDENTIFICADOR NO DECLARADO.");
        finPaso(tabla);
    }

    // ==================================================================
    // Acciones del compilador (llaman a la tabla y muestran el resultado)
    // ==================================================================

    private static void insertar(TablaSimbolo t, String nombre, String cat, String tipo, int tam) {
        Simbolo s = t.insertar(nombre, cat, tipo, tam);
        if (s != null) {
            String desp = s.getDesplazamiento() < 0 ? "" : ", despl. " + s.getDesplazamiento();
            System.out.println("   " + VERDE + "[INSERTAR]" + RESET + " " + NEGRITA + nombre + RESET
                    + " : " + tipo + GRIS + "   -> " + cat + ", ambito '" + s.getAmbito()
                    + "', nivel " + s.getNivel() + desp + RESET);
        } else {
            System.out.println("   " + ROJO + NEGRITA + "[ERROR]" + RESET + ROJO + "    '" + nombre
                    + "' ya fue declarado en el ambito '" + t.ambitoActual() + "' (redeclaracion)" + RESET);
        }
    }

    private static Simbolo buscar(TablaSimbolo t, String nombre) {
        Simbolo s = t.buscar(nombre);
        System.out.println("   " + AZUL + "[BUSCAR]" + RESET + "   " + NEGRITA + nombre + RESET);
        for (String r : t.getUltimoRecorrido()) {
            String c = r.endsWith("ENCONTRADO") ? VERDE : GRIS;
            System.out.println("              " + c + "-> " + r + RESET);
        }
        if (s != null) {
            System.out.println("              " + VERDE + "= " + s.getTipo() + " (" + s.getCategoria() + ")" + RESET);
        } else {
            System.out.println("   " + ROJO + NEGRITA + "[ERROR]" + RESET + ROJO + "    '" + nombre
                    + "' no ha sido declarado" + RESET);
        }
        return s;
    }

    private static void entrar(TablaSimbolo t, String nombre) {
        t.entrarAmbito(nombre);
        System.out.println("   " + MAGENTA + "[ENTRAR]" + RESET + "   se abre el ambito '" + nombre
                + "' (nivel " + t.nivelActual() + ")");
    }

    private static void salir(TablaSimbolo t) {
        String nombre = t.ambitoActual();
        int nivel = t.nivelActual();
        List<Simbolo> borrados = t.salirAmbito();
        StringBuilder sb = new StringBuilder();
        for (Simbolo s : borrados) sb.append(s.getNombre()).append(" ");
        System.out.println("   " + MAGENTA + "[SALIR]" + RESET + "    se cierra el ambito '" + nombre
                + "' (nivel " + nivel + ") y se eliminan: " + sb.toString().trim());
    }

    private static void verificar(boolean ok, String mensaje) {
        if (ok) {
            System.out.println("   " + VERDE + NEGRITA + "[OK]" + RESET + VERDE + "       " + mensaje + RESET);
        } else {
            System.out.println("   " + ROJO + NEGRITA + "[ERROR]" + RESET + ROJO + "    " + mensaje + RESET);
        }
    }

    // ==================================================================
    // Presentacion en pantalla
    // ==================================================================

    private static void inicioPaso(TablaSimbolo t, String[] programa, int... lineas) {
        pasoActual++;
        String linea = "=".repeat(70);
        System.out.println();
        System.out.println(CIAN + linea + RESET);
        System.out.println(CIAN + NEGRITA + " PASO " + pasoActual + " de " + totalPasos + RESET);
        System.out.println(CIAN + linea + RESET);

        System.out.println();
        System.out.println(NEGRITA + " Codigo fuente:" + RESET);
        for (int i = 0; i < programa.length; i++) {
            boolean marcada = false;
            for (int l : lineas) if (l == i + 1) marcada = true;
            String num = String.format("%3d", i + 1);
            if (marcada) {
                System.out.println("  " + AMARILLO + NEGRITA + ">> " + num + " | " + programa[i] + RESET);
            } else {
                System.out.println("     " + GRIS + num + " | " + programa[i] + RESET);
            }
        }

        System.out.println();
        System.out.println(NEGRITA + " Que hace el compilador:" + RESET);
    }

    private static void explicar(String... lineas) {
        System.out.println();
        System.out.println(NEGRITA + " Explicacion:" + RESET);
        for (String l : lineas) {
            System.out.println("   " + l);
        }
    }

    private static void finPaso(TablaSimbolo t) {
        System.out.println();
        System.out.println(NEGRITA + " Pila de ambitos " + GRIS + "(el de arriba es el actual):" + RESET);
        List<String> nombres = t.getNombresAmbitos();
        List<List<Simbolo>> simbolos = t.getSimbolosPorAmbito();
        int nivel = t.nivelActual();
        for (int i = 0; i < nombres.size(); i++) {
            StringBuilder sb = new StringBuilder();
            for (Simbolo s : simbolos.get(i)) sb.append(s.resumen()).append("   ");
            String contenido = sb.length() == 0 ? GRIS + "(vacio)" + RESET : sb.toString().trim();
            String marca = (i == 0) ? AMARILLO + " <- actual" + RESET : "";
            System.out.println("   " + MAGENTA + "+-- nivel " + nivel + "  "
                    + String.format("%-7s", nombres.get(i)) + RESET + ": " + contenido + marca);
            nivel--;
        }
        pausa();
    }

    private static void imprimirTabla(TablaSimbolo t) {
        String linea = "+--------+------------+----------------------+----------+-------+--------+";
        System.out.println();
        System.out.println(NEGRITA + " Tabla de simbolos completa:" + RESET);
        System.out.println("   " + linea);
        System.out.println("   " + NEGRITA
                + "| Nombre | Categoria  | Tipo                 | Ambito   | Nivel | Despl. |" + RESET);
        System.out.println("   " + linea);
        for (List<Simbolo> nivel : t.getSimbolosPorAmbito()) {
            for (Simbolo s : nivel) {
                String c = s.getNombre().equals("x") ? AMARILLO : "";
                System.out.println("   " + c + s + RESET);
            }
        }
        System.out.println("   " + linea);
        System.out.println("   " + AMARILLO + "Las dos x estan resaltadas: misma palabra, distinto ambito." + RESET);
    }

    private static void portada() {
        String linea = "#".repeat(70);
        System.out.println();
        System.out.println(AZUL + NEGRITA + linea + RESET);
        System.out.println(AZUL + NEGRITA + "#" + RESET + "               PRACTICA: TABLA DE SIMBOLOS");
        System.out.println(AZUL + NEGRITA + "#" + RESET + "               Automatas y Compiladores");
        System.out.println(AZUL + NEGRITA + linea + RESET);
        System.out.println();
        System.out.println(" Este programa simula lo que hace un compilador con la tabla de");
        System.out.println(" simbolos mientras lee un programa, linea por linea.");
        System.out.println();
        System.out.println(NEGRITA + " Leyenda:" + RESET);
        System.out.println("   " + VERDE + "[INSERTAR]" + RESET + " se guarda un identificador en la tabla");
        System.out.println("   " + AZUL + "[BUSCAR]" + RESET + "   se consulta un identificador");
        System.out.println("   " + MAGENTA + "[ENTRAR]" + RESET + "   se abre un ambito nuevo");
        System.out.println("   " + MAGENTA + "[SALIR]" + RESET + "    se cierra un ambito y se borran sus variables");
        System.out.println("   " + VERDE + "[OK]" + RESET + "       la verificacion de tipos es correcta");
        System.out.println("   " + ROJO + "[ERROR]" + RESET + "    el compilador encontro un error");
        pausa();
    }

    private static void seccion(String titulo) {
        String linea = "*".repeat(70);
        System.out.println();
        System.out.println();
        System.out.println(AZUL + NEGRITA + linea + RESET);
        System.out.println(AZUL + NEGRITA + "   " + titulo + RESET);
        System.out.println(AZUL + NEGRITA + linea + RESET);
    }

    private static void cierre() {
        String linea = "#".repeat(70);
        System.out.println();
        System.out.println(AZUL + NEGRITA + linea + RESET);
        System.out.println(NEGRITA + " Resumen:" + RESET);
        System.out.println("   - La tabla guarda cada identificador con su tipo, categoria y ambito.");
        System.out.println("   - Cada funcion o bloque abre un ambito nuevo; al cerrarlo se borra.");
        System.out.println("   - La busqueda va del ambito actual hacia el global.");
        System.out.println("   - Gracias a ella se detectan redeclaraciones y variables no declaradas.");
        System.out.println(AZUL + NEGRITA + linea + RESET);
        System.out.println();
    }

    private static void pausa() {
        if (!pausas) return;
        System.out.println();
        System.out.print(GRIS + " Presiona ENTER para continuar..." + RESET);
        if (teclado.hasNextLine()) teclado.nextLine();
    }

    private static void configurarColores() {
        RESET    = color ? "\u001B[0m"  : "";
        NEGRITA  = color ? "\u001B[1m"  : "";
        ROJO     = color ? "\u001B[31m" : "";
        VERDE    = color ? "\u001B[32m" : "";
        AMARILLO = color ? "\u001B[33m" : "";
        AZUL     = color ? "\u001B[34m" : "";
        MAGENTA  = color ? "\u001B[35m" : "";
        CIAN     = color ? "\u001B[36m" : "";
        GRIS     = color ? "\u001B[90m" : "";
    }
}
