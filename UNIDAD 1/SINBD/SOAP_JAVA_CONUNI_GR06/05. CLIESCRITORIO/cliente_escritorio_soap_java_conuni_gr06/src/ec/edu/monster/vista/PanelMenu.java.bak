package ec.edu.monster.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.net.URL;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Panel de menu con 3 tarjetas: Longitud, Masa y Temperatura. Cada tarjeta
 * notifica al Controlador con la clave de la categoria seleccionada.
 */
public class PanelMenu extends JPanel {

    private final JLabel lblSaludo = new JLabel("Bienvenido");
    private final JButton btnCerrarSesion = new JButton("Cerrar Sesion");
    private final JButton btnLongitud;
    private final JButton btnMasa;
    private final JButton btnTemperatura;

    private Consumer<String> accionCategoria;
    private Runnable accionCerrarSesion;

    public PanelMenu() {
        setLayout(new BorderLayout());
        setBackground(Paleta.GRIS_FONDO);

        add(construirEncabezado(), BorderLayout.NORTH);
        btnLongitud    = crearTarjeta("Longitud",    "Metros, pies, millas, pulgadas...");
        btnMasa        = crearTarjeta("Masa",        "Kilogramos, libras, onzas...");
        btnTemperatura = crearTarjeta("Temperatura", "Celsius, Fahrenheit, Kelvin");

        JPanel centro = new JPanel(new GridLayout(1, 3, 16, 0));
        centro.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        centro.setBackground(Paleta.GRIS_FONDO);
        centro.add(btnLongitud);
        centro.add(btnMasa);
        centro.add(btnTemperatura);
        add(centro, BorderLayout.CENTER);

        conectarEventos();
    }

    private JPanel construirEncabezado() {
        JPanel barra = new JPanel(new BorderLayout(10, 0));
        barra.setBackground(Paleta.AZUL);
        barra.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JPanel izq = new JPanel();
        izq.setLayout(new BoxLayout(izq, BoxLayout.X_AXIS));
        izq.setOpaque(false);

        JLabel lblLogo = new JLabel();
        URL url = getClass().getResource("/img/moster.png");
        if (url != null) {
            Image img = new ImageIcon(url).getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(img));
        }
        izq.add(lblLogo);
        izq.add(Box.createHorizontalStrut(10));

        JLabel lblTitulo = new JLabel("Cliente Escritorio CONUNI");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(Paleta.ETIQUETA);
        izq.add(lblTitulo);

        barra.add(izq, BorderLayout.WEST);

        JPanel der = new JPanel();
        der.setOpaque(false);
        der.setLayout(new BoxLayout(der, BoxLayout.X_AXIS));
        lblSaludo.setForeground(Paleta.AMARILLO);
        lblSaludo.setFont(Paleta.SUBTITULO);
        der.add(lblSaludo);
        der.add(Box.createHorizontalStrut(16));
        btnCerrarSesion.setFocusPainted(false);
        der.add(btnCerrarSesion);
        barra.add(der, BorderLayout.EAST);

        return barra;
    }

    private JButton crearTarjeta(String titulo, String descripcion) {
        JButton tarjeta = new JButton("<html><div style='text-align:center;'>"
                + "<div style='font-size:18px;font-weight:bold;'>" + titulo + "</div>"
                + "<div style='font-size:11px;color:#D0DAE8;margin-top:6px;'>"
                + descripcion + "</div></div></html>");
        tarjeta.setBackground(Paleta.AZUL);
        tarjeta.setForeground(Color.WHITE);
        tarjeta.setFocusPainted(false);
        tarjeta.setBorderPainted(false);
        tarjeta.setOpaque(true);
        tarjeta.setContentAreaFilled(true);
        tarjeta.setPreferredSize(new Dimension(220, 140));
        tarjeta.setHorizontalAlignment(SwingConstants.CENTER);
        return tarjeta;
    }

    private void conectarEventos() {
        btnLongitud.addActionListener(e    -> notificar("longitud"));
        btnMasa.addActionListener(e        -> notificar("masa"));
        btnTemperatura.addActionListener(e -> notificar("temperatura"));
        btnCerrarSesion.addActionListener(e -> {
            if (accionCerrarSesion != null) accionCerrarSesion.run();
        });
    }

    private void notificar(String categoria) {
        if (accionCategoria != null) accionCategoria.accept(categoria);
    }

    public void setUsuario(String usuario) {
        lblSaludo.setText("Bienvenido, " + usuario);
    }

    public void setOnCategoriaSeleccionada(Consumer<String> accion) {
        this.accionCategoria = accion;
    }

    public void setOnCerrarSesion(Runnable accion) {
        this.accionCerrarSesion = accion;
    }
}
