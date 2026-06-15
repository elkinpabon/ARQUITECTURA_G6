package ec.edu.monster.vista;

import ec.edu.monster.controlador.TicketController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 * Ventana principal: barra superior corporativa + pestanas
 * Partidos / Carrito / Mis compras / Mi cuenta (+ Administracion si ADMIN).
 */
public class MainPanel extends JPanel {

    private final TicketController ctrl;
    private final JTabbedPane tabs = new JTabbedPane();
    private int idxCarrito = -1;

    public MainPanel(TicketController ctrl, Runnable onLogout) {
        this.ctrl = ctrl;
        setLayout(new BorderLayout());
        setBackground(Estilo.FONDO);

        /* ----- Barra superior ----- */
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Estilo.AZUL);
        barra.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JPanel marca = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        marca.setOpaque(false);
        JLabel logo = Img.label("/images/moster.png", 34);
        JLabel titulo = new JLabel("TICKETPREMIUM | FIFA 2026");
        titulo.setFont(Estilo.fuente(Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);
        marca.add(logo);
        marca.add(titulo);
        barra.add(marca, BorderLayout.WEST);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setOpaque(false);
        JLabel usuario = new JLabel(ctrl.getSesion().getNombre()
                + " (" + (ctrl.getSesion().isAdmin() ? "ADMIN" : "CLIENTE") + ")");
        usuario.setFont(Estilo.fuente(Font.PLAIN, 12));
        usuario.setForeground(new Color(0xCFE0F0));
        JButton btnSalir = Estilo.botonPeligro("Cerrar sesion");
        btnSalir.addActionListener(e -> onLogout.run());
        derecha.add(usuario);
        derecha.add(btnSalir);
        barra.add(derecha, BorderLayout.EAST);
        add(barra, BorderLayout.NORTH);

        /* ----- Pestanas ----- */
        tabs.setFont(Estilo.fuente(Font.BOLD, 13));
        tabs.setBackground(Estilo.FONDO);

        tabs.addTab("Partidos", new PartidosPanel(ctrl));
        idxCarrito = tabs.getTabCount();
        tabs.addTab("Carrito (0)", new CarritoPanel(ctrl));
        ComprasPanel compras = new ComprasPanel(ctrl);
        tabs.addTab("Mis compras", compras);
        CuentaPanel cuenta = new CuentaPanel(ctrl);
        tabs.addTab("Mi cuenta", cuenta);
        if (ctrl.getSesion().isAdmin()) {
            tabs.addTab("Administracion", new AdminPanel(ctrl));
        }
        add(tabs, BorderLayout.CENTER);

        // contador del carrito en la pestana
        ctrl.getCarrito().onCambio(() -> SwingUtilities.invokeLater(() ->
                tabs.setTitleAt(idxCarrito,
                        "Carrito (" + ctrl.getCarrito().size() + ")")));

        // refrescar compras/cuenta al entrar a esas pestanas
        tabs.addChangeListener(e -> {
            java.awt.Component c = tabs.getSelectedComponent();
            if (c == compras) compras.cargar();
            else if (c == cuenta) cuenta.cargar();
        });
    }
}
