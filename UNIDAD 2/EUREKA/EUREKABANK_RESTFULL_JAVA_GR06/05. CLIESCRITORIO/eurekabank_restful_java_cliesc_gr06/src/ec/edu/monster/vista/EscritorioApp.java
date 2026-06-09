package ec.edu.monster.vista;

import ec.edu.monster.controlador.BancoController;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Cliente de Escritorio (Swing) — MVC, misma funcionalidad que el cliente web.
 * La conexión al servidor se configura en servidor.properties (ServidorConfig).
 */
public class EscritorioApp extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final BancoController ctrl = new BancoController();

    public EscritorioApp() {
        setTitle("EUREKABANK GR06 — Banca RESTFULL · Cliente Escritorio");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 640);
        setMinimumSize(new java.awt.Dimension(560, 480));
        setLocationRelativeTo(null);
        getContentPane().setLayout(cards);

        LoginPanel login = new LoginPanel(ctrl, this::mostrarMenu);
        getContentPane().add(login, "login");
        showLogin();
    }

    public final void showLogin() {
        cards.show(getContentPane(), "login");
    }

    private void mostrarMenu() {
        MainPanel main = new MainPanel(ctrl, this::cerrarSesion);
        getContentPane().add(main, "main");
        cards.show(getContentPane(), "main");
        revalidate();
    }

    private void cerrarSesion() {
        ctrl.logout();
        showLogin();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EscritorioApp().setVisible(true));
    }
}
