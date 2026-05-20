package ec.edu.monster.vista;

import ec.edu.monster.config.ServidorConfig;
import ec.edu.monster.controlador.TicketController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/** Vista de login del cliente escritorio TicketPremium. */
public class LoginPanel extends JPanel {

    public LoginPanel(TicketController ctrl, Runnable onOk) {
        setLayout(new BorderLayout());

        // ----- Banner izquierdo con login.jpg -----
        JLabel banner = Img.label("/images/login.jpg", 540);
        banner.setHorizontalAlignment(SwingConstants.CENTER);
        banner.setBackground(new Color(20, 30, 50));
        banner.setOpaque(true);
        add(banner, BorderLayout.WEST);

        // ----- Formulario derecho -----
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridwidth = 2;

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

        g.gridwidth = 1;
        JTextField txtUsuario = new JTextField(18);
        JPasswordField txtClave = new JPasswordField(18);

        g.gridx = 0; g.gridy = 3; form.add(new JLabel("Usuario:"), g);
        g.gridx = 1;              form.add(txtUsuario, g);
        g.gridx = 0; g.gridy = 4; form.add(new JLabel("Contrasena:"), g);
        g.gridx = 1;              form.add(txtClave, g);

        JButton btn = new JButton("Iniciar sesion");
        btn.setFont(btn.getFont().deriveFont(Font.BOLD));
        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        form.add(btn, g);

        JLabel ayuda = new JLabel(
            "<html><center>monster / monster9 (ADMIN)<br>"
          + "josue, mikaela, elkin / admin2002 (CLIENTE)</center></html>",
          SwingConstants.CENTER);
        ayuda.setForeground(new Color(120, 120, 120));
        g.gridy = 6;
        form.add(ayuda, g);

        JLabel servidor = new JLabel("Servidor: " + ServidorConfig.base(),
                SwingConstants.CENTER);
        servidor.setForeground(new Color(150, 150, 150));
        servidor.setFont(servidor.getFont().deriveFont(Font.PLAIN, 11f));
        g.gridy = 7;
        form.add(servidor, g);

        add(form, BorderLayout.CENTER);

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
    }
}
