package ec.edu.monster.vista;

import ec.edu.monster.config.ServidorConfig;
import ec.edu.monster.controlador.BancoController;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/** Vista de login. */
public class LoginPanel extends JPanel {

    public LoginPanel(BancoController ctrl, Runnable onOk) {
        setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtUsuario = new JTextField(18);
        JPasswordField txtClave = new JPasswordField(18);
        JButton btn = new JButton("Iniciar sesión");

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        add(Img.label("/images/moster.png", 120), g);
        g.gridy = 1;
        JLabel titulo = new JLabel("EUREKABANK GR06 — Iniciar sesión");
        add(titulo, g);
        g.gridwidth = 1;
        g.gridx = 0; g.gridy = 2; add(new JLabel("Usuario:"), g);
        g.gridx = 1; add(txtUsuario, g);
        g.gridx = 0; g.gridy = 3; add(new JLabel("Clave:"), g);
        g.gridx = 1; add(txtClave, g);
        g.gridx = 1; g.gridy = 4; add(btn, g);
        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        add(new JLabel("Servidor: " + ServidorConfig.base()), g);

        Runnable accion = () -> {
            try {
                if (ctrl.login(txtUsuario.getText().trim(),
                        new String(txtClave.getPassword()).trim())) {
                    txtClave.setText("");
                    onOk.run();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Usuario o clave inválidos.", "Error",
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
