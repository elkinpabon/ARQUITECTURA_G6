package ec.edu.monster.vista;

import ec.edu.monster.controlador.TicketController;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Cliente Escritorio TicketPremium (Swing, MVC).
 * La conexion al servidor se configura en servidor.properties (ServidorConfig).
 */
public class EscritorioApp extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final TicketController ctrl = new TicketController();

    public EscritorioApp() {
        setTitle("TICKETPREMIUM GR06 — Cliente Escritorio");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 680);
        setMinimumSize(new java.awt.Dimension(640, 520));
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
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) { }
        SwingUtilities.invokeLater(() -> new EscritorioApp().setVisible(true));
    }
}
