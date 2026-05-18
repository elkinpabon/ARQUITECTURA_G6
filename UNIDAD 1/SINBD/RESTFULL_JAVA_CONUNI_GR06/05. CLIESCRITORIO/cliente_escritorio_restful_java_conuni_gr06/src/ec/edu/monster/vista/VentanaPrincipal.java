package ec.edu.monster.vista;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Ventana raiz de la aplicacion. Contiene un {@link CardLayout} con los tres
 * paneles (Login, Menu, Conversion) y expone metodos para navegar entre ellos.
 * No contiene logica de negocio — la navega el Controlador.
 */
public class VentanaPrincipal extends JFrame {

    public static final String TARJETA_LOGIN     = "LOGIN";
    public static final String TARJETA_MENU      = "MENU";
    public static final String TARJETA_CONVERSOR = "CONVERSOR";

    private final CardLayout layoutTarjetas = new CardLayout();
    private final JPanel contenedor = new JPanel(layoutTarjetas);

    private final PanelLogin panelLogin = new PanelLogin();
    private final PanelMenu panelMenu = new PanelMenu();
    private final PanelConversion panelConversion = new PanelConversion();

    public VentanaPrincipal() {
        setTitle("Cliente Escritorio CONUNI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 560);
        setLocationRelativeTo(null);
        setResizable(true);

        contenedor.add(panelLogin, TARJETA_LOGIN);
        contenedor.add(panelMenu, TARJETA_MENU);
        contenedor.add(panelConversion, TARJETA_CONVERSOR);

        getContentPane().add(contenedor);
    }

    public void mostrar(String tarjeta) {
        layoutTarjetas.show(contenedor, tarjeta);
    }

    public PanelLogin getPanelLogin()         { return panelLogin; }
    public PanelMenu getPanelMenu()           { return panelMenu; }
    public PanelConversion getPanelConversion() { return panelConversion; }
}
