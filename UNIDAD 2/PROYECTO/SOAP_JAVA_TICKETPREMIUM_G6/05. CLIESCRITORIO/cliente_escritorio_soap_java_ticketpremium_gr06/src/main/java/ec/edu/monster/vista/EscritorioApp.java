package ec.edu.monster.vista;

import ec.edu.monster.controlador.TicketController;
import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Cliente Escritorio TicketPremium FIFA 2026 (Swing, MVC).
 * La conexion al servidor se configura en servidor.properties (ServidorConfig).
 * Al cerrar la ventana o la sesion se liberan las reservas pendientes.
 */
public class EscritorioApp extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final TicketController ctrl = new TicketController();
    private MainPanel main;

    public EscritorioApp() {
        setTitle("TICKETPREMIUM FIFA 2026 - Cliente Escritorio GR06");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1180, 740);
        setMinimumSize(new java.awt.Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setLayout(cards);
        getContentPane().setBackground(Estilo.FONDO);

        // al salir de la app: liberar mis reservas en el servidor
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                ctrl.liberarMisReservasSilencioso();
            }
        });

        LoginPanel login = new LoginPanel(ctrl, this::mostrarMenu);
        getContentPane().add(login, "login");
        showLogin();
    }

    public final void showLogin() {
        cards.show(getContentPane(), "login");
    }

    private void mostrarMenu() {
        if (main != null) getContentPane().remove(main);
        main = new MainPanel(ctrl, this::cerrarSesion);
        getContentPane().add(main, "main");
        cards.show(getContentPane(), "main");
        revalidate();
    }

    private void cerrarSesion() {
        ctrl.logout();   // libera reservas + limpia carrito y sesion
        showLogin();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) { }
        SwingUtilities.invokeLater(() -> new EscritorioApp().setVisible(true));
    }
}
