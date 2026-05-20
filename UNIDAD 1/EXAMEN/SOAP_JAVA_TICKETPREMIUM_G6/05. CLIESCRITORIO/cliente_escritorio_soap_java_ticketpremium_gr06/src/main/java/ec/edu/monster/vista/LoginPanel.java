package ec.edu.monster.vista;

import ec.edu.monster.config.ServidorConfig;
import ec.edu.monster.controlador.TicketController;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/** Vista de login del cliente escritorio TicketPremium (responsive). */
public class LoginPanel extends JPanel {

    private static final int FORM_PREF_W = 400;
    private static final int FORM_MIN_W  = 320;
    private static final int LABEL_W     = 95;

    public LoginPanel(TicketController ctrl, Runnable onOk) {
        setLayout(new GridBagLayout());

        GridBagConstraints rootG = new GridBagConstraints();
        rootG.fill   = GridBagConstraints.BOTH;
        rootG.weighty = 1.0;

        // ----- Banner izquierdo (escalable) -----
        JComponent banner = Img.bannerEscalable(
                "/images/login.jpg", new Color(20, 30, 50));
        banner.setMinimumSize(new Dimension(0, 0));
        banner.setPreferredSize(new Dimension(400, 600));
        rootG.gridx = 0;
        rootG.weightx = 1.0;
        add(banner, rootG);

        // ----- Formulario derecho (ancho fijo, centrado vertical) -----
        JPanel form = construirForm(ctrl, onOk);
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        wrapper.add(form, new GridBagConstraints());

        rootG.gridx = 1;
        rootG.weightx = 0.0;     // el form mantiene su ancho preferido
        add(wrapper, rootG);
    }

    private JPanel construirForm(TicketController ctrl, Runnable onOk) {
        JPanel form = new JPanel(new GridBagLayout());
        form.setPreferredSize(new Dimension(FORM_PREF_W, 480));
        form.setMinimumSize(new Dimension(FORM_MIN_W, 420));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridwidth = 2;
        g.weightx = 1.0;

        JLabel logo = Img.label("/images/moster.png", 100);
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridx = 0; g.gridy = 0;
        form.add(logo, g);

        JLabel titulo = new JLabel("TicketPremium", SwingConstants.CENTER);
        titulo.setFont(new Font("Sans-Serif", Font.BOLD, 24));
        g.gridy = 1;
        form.add(titulo, g);

        JLabel sub = new JLabel("Iniciar sesion - GR06", SwingConstants.CENTER);
        sub.setForeground(new Color(100, 100, 100));
        g.gridy = 2;
        form.add(sub, g);

        // Campos
        JTextField txtUsuario = new JTextField(16);
        JPasswordField txtClave = new JPasswordField(16);

        JLabel lblUsuario = new JLabel("Usuario:");
        JLabel lblClave   = new JLabel("Contrasena:");
        Dimension labelDim = new Dimension(LABEL_W, 26);
        lblUsuario.setPreferredSize(labelDim);
        lblUsuario.setMinimumSize(labelDim);
        lblClave.setPreferredSize(labelDim);
        lblClave.setMinimumSize(labelDim);

        g.gridwidth = 1;
        g.weightx = 0.0; g.fill = GridBagConstraints.NONE;
        g.gridx = 0; g.gridy = 3; form.add(lblUsuario, g);
        g.weightx = 1.0; g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 1;              form.add(txtUsuario, g);

        g.weightx = 0.0; g.fill = GridBagConstraints.NONE;
        g.gridx = 0; g.gridy = 4; form.add(lblClave, g);
        g.weightx = 1.0; g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 1;              form.add(txtClave, g);

        // Boton
        JButton btn = new JButton("Iniciar sesion");
        btn.setFont(btn.getFont().deriveFont(Font.BOLD));
        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        g.weightx = 1.0; g.fill = GridBagConstraints.HORIZONTAL;
        form.add(btn, g);

        JLabel ayuda = new JLabel(
            "<html><center>monster / monster9 (ADMIN)<br>"
          + "josue, mikaela, elkin / admin2002 (CLIENTE)</center></html>",
          SwingConstants.CENTER);
        ayuda.setForeground(new Color(120, 120, 120));
        g.gridy = 6;
        form.add(ayuda, g);

        JLabel servidor = new JLabel(
                "<html><center>Servidor:<br>" + ServidorConfig.base() + "</center></html>",
                SwingConstants.CENTER);
        servidor.setForeground(new Color(150, 150, 150));
        servidor.setFont(servidor.getFont().deriveFont(Font.PLAIN, 11f));
        g.gridy = 7;
        form.add(servidor, g);

        Runnable accion = () -> {
            try {
                if (ctrl.login(txtUsuario.getText().trim(),
                        new String(txtClave.getPassword()).trim())) {
                    txtClave.setText("");
                    onOk.run();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Usuario o contrasena invalidos.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error contactando el servidor:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        btn.addActionListener(e -> accion.run());
        txtClave.addActionListener(e -> accion.run());

        return form;
    }
}
