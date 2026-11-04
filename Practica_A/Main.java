package Practica_A;

import javax.swing.SwingUtilities;

/**
 * Practica A: Tabla de simbolos simple (un solo ambito)
 *
 * Ejecutar:  java -cp out Practica_A.Main
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VentanaPrincipal::new);
    }
}
