package ec.edu.monster;

import ec.edu.monster.controlador.ControladorEscritorio;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicacion de escritorio. Aplica el Look & Feel del
 * sistema y arranca el Controlador dentro del Event Dispatch Thread, como
 * exige Swing.
 */
public class Aplicacion {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignorada) {
                // Si falla, Swing usa el Look & Feel metal por defecto.
            }
            new ControladorEscritorio().iniciar();
        });
    }
}
