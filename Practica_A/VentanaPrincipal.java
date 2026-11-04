package Practica_A;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Interfaz grafica (Swing). Solo lee los campos y muestra resultados;
 * la logica esta en TablaSimbolos.
 */
public class VentanaPrincipal {

    private static final Color VERDE = new Color(0x2E7D32);
    private static final Color ROJO = new Color(0xC62828);

    private final TablaSimbolos tabla = new TablaSimbolos();

    private final JTextField campoNombre = new JTextField(15);
    private final JTextField campoTipo = new JTextField(10);
    private final JButton btnAgregar = new JButton("Agregar variable");
    private final JLabel mensaje = new JLabel(" ");
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[]{"Nombre", "Tipo"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public VentanaPrincipal() {
        JFrame ventana = new JFrame("Tabla de símbolos");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulario.add(new JLabel("Nombre de la variable:"));
        formulario.add(campoNombre);
        formulario.add(new JLabel("Tipo de dato:"));
        formulario.add(campoTipo);
        formulario.add(btnAgregar);

        JTable vistaTabla = new JTable(modeloTabla);
        vistaTabla.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(vistaTabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Tabla de símbolos"));

        JPanel raiz = new JPanel(new BorderLayout(8, 8));
        raiz.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        raiz.add(formulario, BorderLayout.NORTH);
        raiz.add(scroll, BorderLayout.CENTER);
        raiz.add(mensaje, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregar());
        campoTipo.addActionListener(e -> agregar());
        campoNombre.addActionListener(e -> campoTipo.requestFocus());

        ventana.setContentPane(raiz);
        ventana.setSize(640, 380);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private void agregar() {
        String nombre = campoNombre.getText().trim();
        String tipo = campoTipo.getText().trim();

        if (nombre.isEmpty() || tipo.isEmpty()) {
            mostrar("Escribe el nombre y el tipo.", ROJO);
            return;
        }
        if (!tabla.agregar(nombre, tipo)) {
            mostrar("Error: la variable ya existe.", ROJO);
            return;
        }
        refrescarTabla();
        mostrar("Variable agregada correctamente.", VERDE);
        campoNombre.setText("");
        campoTipo.setText("");
        campoNombre.requestFocus();
    }

    private void refrescarTabla() {
        modeloTabla.setRowCount(0);
        for (Simbolo s : tabla.simbolos()) {
            modeloTabla.addRow(new Object[]{s.nombre, s.tipo});
        }
    }

    private void mostrar(String texto, Color color) {
        mensaje.setForeground(color);
        mensaje.setText(texto);
    }
}
