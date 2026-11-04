package Practica_sencilla.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Practica: Tabla de simbolos (interfaz grafica con Swing)
 * Materia: Automatas y Compiladores
 *
 * Ejecutar:  java -cp out Practica_sencilla.gui.Main
 */
public class Main {

    // ---------------- Pasos del programa analizado ----------------

    /** Resultado de un paso: lo que explica la interfaz y si fue un error. */
    private static class Resultado {
        final String texto;
        final boolean error;
        Resultado(String texto, boolean error) { this.texto = texto; this.error = error; }
    }

    private interface Accion { Resultado ejecutar(TablaSimbolo t); }

    private static class Paso {
        final String linea;
        final Accion accion;
        Paso(String linea, Accion accion) { this.linea = linea; this.accion = accion; }
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

    private static List<Paso> programaCorrecto() {
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

    private static List<Paso> programaConErrores() {
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

    // ---------------- Interfaz ----------------

    private static final Color VERDE = new Color(0x2E7D32);
    private static final Color ROJO = new Color(0xC62828);
    private static final Color GRIS = new Color(0x9E9E9E);

    private List<Paso> pasos;
    private TablaSimbolo tabla;
    private int siguiente;

    private final DefaultListModel<String> modeloCodigo = new DefaultListModel<>();
    private final JList<String> listaCodigo = new JList<>(modeloCodigo);
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[]{"Nombre", "Tipo", "Categoría", "Nivel", "Nota"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tablaVista = new JTable(modeloTabla);
    private final JLabel explicacion = new JLabel();
    private final JLabel estado = new JLabel();
    private final JButton btnSiguiente = new JButton("Siguiente ▶");
    private final JButton btnReiniciar = new JButton("Reiniciar");
    private final JComboBox<String> selector = new JComboBox<>(
            new String[]{"Programa correcto", "Programa con errores"});

    private Main() {
        JFrame ventana = new JFrame("Tabla de símbolos - Autómatas y Compiladores");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Codigo
        listaCodigo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        listaCodigo.setSelectionBackground(new Color(0xFFF59D));
        listaCodigo.setSelectionForeground(Color.BLACK);
        listaCodigo.setFocusable(false);
        listaCodigo.setEnabled(false);
        listaCodigo.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean foc) {
                Component c = super.getListCellRendererComponent(l, v, i, sel, foc);
                c.setForeground(i == siguiente - 1 ? Color.BLACK : (i < siguiente ? new Color(0x555555) : new Color(0x222222)));
                setOpaque(true);
                setBackground(i == siguiente - 1 ? new Color(0xFFF59D) : Color.WHITE);
                return c;
            }
        });
        JScrollPane panelCodigo = new JScrollPane(listaCodigo);
        panelCodigo.setBorder(BorderFactory.createTitledBorder("Programa analizado"));

        // Tabla
        tablaVista.setRowHeight(24);
        tablaVista.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        tablaVista.setEnabled(false);
        tablaVista.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, s, f, row, col);
                boolean oculto = "(oculta)".equals(modeloTabla.getValueAt(row, 4));
                c.setForeground(oculto ? GRIS : Color.BLACK);
                return c;
            }
        });
        JScrollPane panelTabla = new JScrollPane(tablaVista);
        panelTabla.setBorder(BorderFactory.createTitledBorder("Tabla de símbolos (del ámbito interno al global)"));

        // Explicacion
        explicacion.setVerticalAlignment(SwingConstants.TOP);
        explicacion.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        JPanel panelExp = new JPanel(new BorderLayout());
        panelExp.setBorder(BorderFactory.createTitledBorder("Qué hace el compilador"));
        panelExp.add(explicacion, BorderLayout.CENTER);
        panelExp.setPreferredSize(new Dimension(0, 110));

        // Controles
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controles.add(selector);
        controles.add(btnReiniciar);
        controles.add(btnSiguiente);
        controles.add(estado);

        JSplitPane centro = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelCodigo, panelTabla);
        centro.setResizeWeight(0.45);

        JPanel raiz = new JPanel(new BorderLayout(8, 8));
        raiz.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        raiz.add(controles, BorderLayout.NORTH);
        raiz.add(centro, BorderLayout.CENTER);
        raiz.add(panelExp, BorderLayout.SOUTH);

        btnSiguiente.addActionListener(e -> avanzar());
        btnReiniciar.addActionListener(e -> reiniciar());
        selector.addActionListener(e -> reiniciar());

        ventana.setContentPane(raiz);
        ventana.setSize(900, 560);
        ventana.setLocationRelativeTo(null);
        reiniciar();
        ventana.setVisible(true);
    }

    private void reiniciar() {
        pasos = selector.getSelectedIndex() == 0 ? programaCorrecto() : programaConErrores();
        tabla = new TablaSimbolo();
        siguiente = 0;
        modeloCodigo.clear();
        for (Paso p : pasos) modeloCodigo.addElement(p.linea);
        listaCodigo.clearSelection();
        mostrarMensaje("Pulsa <b>Siguiente</b> para analizar la primera línea.", Color.DARK_GRAY);
        refrescar();
    }

    private void avanzar() {
        if (siguiente >= pasos.size()) return;
        Resultado r = pasos.get(siguiente).accion.ejecutar(tabla);
        siguiente++;
        mostrarMensaje(r.texto, r.error ? ROJO : VERDE);
        refrescar();
    }

    private void mostrarMensaje(String html, Color color) {
        explicacion.setForeground(color);
        explicacion.setText("<html>" + html + "</html>");
    }

    private void refrescar() {
        listaCodigo.repaint();
        btnSiguiente.setEnabled(siguiente < pasos.size());
        estado.setText("Paso " + siguiente + " de " + pasos.size() + "   |   nivel actual: " + tabla.nivelActual());

        modeloTabla.setRowCount(0);
        Set<String> vistos = new HashSet<>();
        for (Simbolo s : tabla.simbolos()) { // del mas interno al global
            String nota = vistos.add(s.nombre) ? "" : "(oculta)";
            modeloTabla.addRow(new Object[]{s.nombre, s.tipo, s.categoria, s.nivel, nota});
        }
        if (siguiente >= pasos.size()) {
            explicacion.setText(explicacion.getText().replace("</html>",
                    "<br><br><i>Fin del programa: solo queda el ámbito global.</i></html>"));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
