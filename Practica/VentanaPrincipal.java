package Practica;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import Practica.Programas.Paso;
import Practica.Programas.Resultado;

/**
 * Interfaz grafica (Swing). Solo dibuja y reacciona a los botones;
 * la logica esta en TablaSimbolo y Programas.
 */
public class VentanaPrincipal {

    private static final Color VERDE = new Color(0x2E7D32);
    private static final Color ROJO = new Color(0xC62828);
    private static final Color GRIS = new Color(0x9E9E9E);
    private static final Color RESALTADO = new Color(0xFFF59D);

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

    public VentanaPrincipal() {
        JFrame ventana = new JFrame("Tabla de símbolos - Autómatas y Compiladores");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Codigo
        listaCodigo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        listaCodigo.setFocusable(false);
        listaCodigo.setEnabled(false);
        listaCodigo.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean foc) {
                Component c = super.getListCellRendererComponent(l, v, i, sel, foc);
                boolean actual = i == siguiente - 1;
                c.setForeground(actual ? Color.BLACK : (i < siguiente ? new Color(0x555555) : new Color(0x222222)));
                setOpaque(true);
                setBackground(actual ? RESALTADO : Color.WHITE);
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
        pasos = selector.getSelectedIndex() == 0 ? Programas.correcto() : Programas.conErrores();
        tabla = new TablaSimbolo();
        siguiente = 0;
        modeloCodigo.clear();
        for (Paso p : pasos) modeloCodigo.addElement(p.linea);
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
}
