package ec.edu.monster.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;
import java.util.function.BiConsumer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Panel de login. Layout en dos columnas (imagen a la izquierda, formulario
 * a la derecha) siguiendo la estetica del cliente web.
 *
 * Publica {@code setOnLogin(BiConsumer)} para que el Controlador reaccione al
 * evento sin que el Panel conozca al modelo. Metodos {@code mostrarError} y
 * {@code limpiar} permiten al Controlador actualizar el estado visual.
 */
public class PanelLogin extends JPanel {

    private final JTextField campoUsuario = new JTextField(18);
    private final JPasswordField campoContrasena = new JPasswordField(18);
    private final JButton botonMostrar = new JButton("Mostrar");
    private final JButton botonIngresar = new JButton("Ingresar");
    private final JLabel lblError = new JLabel(" ");

    private BiConsumer<String, String> accionLogin;

    public PanelLogin() {
        setLayout(new GridLayout(1, 2));
        setBackground(Color.WHITE);

        add(construirPanelImagen());
        add(construirPanelFormulario());

        conectarEventos();
    }

    private JPanel construirPanelImagen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Paleta.AZUL);

        JLabel lblImagen = new JLabel();
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        URL urlImagen = getClass().getResource("/img/login.jpg");
        if (urlImagen != null) {
            ImageIcon icono = new ImageIcon(urlImagen);
            Image escalada = icono.getImage().getScaledInstance(380, 480, Image.SCALE_SMOOTH);
            lblImagen.setIcon(new ImageIcon(escalada));
        } else {
            lblImagen.setText("Imagen login.jpg no encontrada en /img");
            lblImagen.setForeground(Color.WHITE);
        }
        panel.add(lblImagen, BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 36, 30, 36));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 0, 6, 0);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;

        // Logo redondeado
        JLabel lblLogo = new JLabel();
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        URL urlLogo = getClass().getResource("/img/moster.png");
        if (urlLogo != null) {
            ImageIcon icono = new ImageIcon(urlLogo);
            Image escalada = icono.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(escalada));
        }
        panel.add(lblLogo, c);

        c.gridy++;
        JLabel lblTitulo = new JLabel("Iniciar Sesion");
        lblTitulo.setFont(Paleta.TITULO);
        lblTitulo.setForeground(Paleta.AZUL);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblTitulo, c);

        c.gridy++;
        JLabel lblSub = new JLabel("Ingresa tus credenciales");
        lblSub.setFont(Paleta.SUBTITULO);
        lblSub.setForeground(Paleta.TEXTO_SUAVE);
        lblSub.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblSub, c);

        c.gridy++;
        c.gridwidth = 1;
        c.gridx = 0;
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(Paleta.ETIQUETA);
        panel.add(lblUsuario, c);

        c.gridy++;
        campoUsuario.setFont(Paleta.CAMPO);
        campoUsuario.setPreferredSize(new Dimension(240, 30));
        panel.add(campoUsuario, c);

        c.gridy++;
        JLabel lblContrasena = new JLabel("Contrasena:");
        lblContrasena.setFont(Paleta.ETIQUETA);
        panel.add(lblContrasena, c);

        c.gridy++;
        JPanel filaContrasena = new JPanel(new BorderLayout(6, 0));
        filaContrasena.setOpaque(false);
        campoContrasena.setFont(Paleta.CAMPO);
        campoContrasena.setPreferredSize(new Dimension(180, 30));
        filaContrasena.add(campoContrasena, BorderLayout.CENTER);
        botonMostrar.setFocusPainted(false);
        filaContrasena.add(botonMostrar, BorderLayout.EAST);
        panel.add(filaContrasena, c);

        c.gridy++;
        botonIngresar.setBackground(Paleta.AZUL);
        botonIngresar.setForeground(Color.WHITE);
        botonIngresar.setFont(Paleta.ETIQUETA);
        botonIngresar.setFocusPainted(false);
        botonIngresar.setOpaque(true);
        botonIngresar.setBorderPainted(false);
        botonIngresar.setPreferredSize(new Dimension(240, 36));
        panel.add(botonIngresar, c);

        c.gridy++;
        lblError.setForeground(Paleta.ROJO_ERROR_FG);
        lblError.setFont(Paleta.SUBTITULO);
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblError, c);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.setBackground(Color.WHITE);
        wrapper.add(panel);
        return wrapper;
    }

    private void conectarEventos() {
        botonMostrar.addActionListener(e -> {
            if (campoContrasena.getEchoChar() != (char) 0) {
                campoContrasena.setEchoChar((char) 0);
                botonMostrar.setText("Ocultar");
            } else {
                campoContrasena.setEchoChar('•');
                botonMostrar.setText("Mostrar");
            }
        });

        botonIngresar.addActionListener(e -> dispararLogin());
        campoUsuario.addActionListener(e -> campoContrasena.requestFocusInWindow());
        campoContrasena.addActionListener(e -> dispararLogin());
    }

    private void dispararLogin() {
        if (accionLogin != null) {
            accionLogin.accept(
                campoUsuario.getText().trim(),
                new String(campoContrasena.getPassword())
            );
        }
    }

    public void setOnLogin(BiConsumer<String, String> accion) {
        this.accionLogin = accion;
    }

    public void mostrarError(String mensaje) {
        lblError.setText(mensaje == null ? " " : mensaje);
    }

    public void setBotonHabilitado(boolean habilitado) {
        botonIngresar.setEnabled(habilitado);
        botonIngresar.setText(habilitado ? "Ingresar" : "Ingresando...");
    }

    public void limpiar() {
        campoUsuario.setText("");
        campoContrasena.setText("");
        lblError.setText(" ");
    }
}
