package ec.edu.monster.vista;

import java.awt.Color;
import java.awt.Image;
import java.net.URL;
import java.util.function.BiConsumer;
import javax.swing.ImageIcon;

/**
 * Panel de login en formato Matisse (archivo .form emparejado).
 * Puedes abrirlo en NetBeans con clic derecho -> Open -> pestana "Design"
 * para editarlo visualmente con drag-and-drop.
 *
 * El metodo initComponents() es generado/mantenido por NetBeans, no lo
 * edites a mano. La logica de UI (imagenes, eventos, MVC) vive en
 * configurarVista() y conectarEventos(), fuera del area generada.
 */
public class PanelLogin extends javax.swing.JPanel {

    private BiConsumer<String, String> accionLogin;

    public PanelLogin() {
        initComponents();
        configurarVista();
        conectarEventos();
    }

    /** Carga la imagen y el logo fuera del area generada por Matisse. */
    private void configurarVista() {
        setBackground(Color.WHITE);

        URL urlImagen = getClass().getResource("/img/login.jpg");
        if (urlImagen != null) {
            Image img = new ImageIcon(urlImagen).getImage()
                    .getScaledInstance(320, 380, Image.SCALE_SMOOTH);
            lblImagen.setIcon(new ImageIcon(img));
            lblImagen.setText("");
        }

        URL urlLogo = getClass().getResource("/img/moster.png");
        if (urlLogo != null) {
            Image img = new ImageIcon(urlLogo).getImage()
                    .getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(img));
            lblLogo.setText("");
        }

        lblTitulo.setForeground(Paleta.AZUL);
        lblTitulo.setFont(Paleta.TITULO);
        lblSubtitulo.setForeground(Paleta.TEXTO_SUAVE);

        botonIngresar.setBackground(Paleta.AZUL);
        botonIngresar.setForeground(Color.WHITE);
        botonIngresar.setOpaque(true);
        botonIngresar.setBorderPainted(false);

        lblError.setForeground(Paleta.ROJO_ERROR_FG);
    }

    private void conectarEventos() {
        botonMostrar.addActionListener(e -> toggleVisibilidad());
        botonIngresar.addActionListener(e -> dispararLogin());
        campoUsuario.addActionListener(e -> campoContrasena.requestFocusInWindow());
        campoContrasena.addActionListener(e -> dispararLogin());
    }

    private void toggleVisibilidad() {
        if (campoContrasena.getEchoChar() != (char) 0) {
            campoContrasena.setEchoChar((char) 0);
            botonMostrar.setText("Ocultar");
        } else {
            campoContrasena.setEchoChar('•');
            botonMostrar.setText("Mostrar");
        }
    }

    private void dispararLogin() {
        if (accionLogin != null) {
            accionLogin.accept(
                campoUsuario.getText().trim(),
                new String(campoContrasena.getPassword())
            );
        }
    }

    // ========= API publica consumida por ControladorEscritorio =========

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

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblImagen = new javax.swing.JLabel();
        lblLogo = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        lblUsuario = new javax.swing.JLabel();
        campoUsuario = new javax.swing.JTextField();
        lblContrasena = new javax.swing.JLabel();
        campoContrasena = new javax.swing.JPasswordField();
        botonMostrar = new javax.swing.JButton();
        botonIngresar = new javax.swing.JButton();
        lblError = new javax.swing.JLabel();

        lblImagen.setBackground(new java.awt.Color(31, 58, 95));
        lblImagen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImagen.setText("Imagen login");
        lblImagen.setOpaque(true);

        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setText("Logo");

        lblTitulo.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("ec/edu/monster/vista/Bundle"); // NOI18N
        lblTitulo.setText(bundle.getString("PanelLogin.lblTitulo.text")); // NOI18N

        lblSubtitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSubtitulo.setText("Ingresa tus credenciales");

        lblUsuario.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        lblUsuario.setText("Usuario:");

        lblContrasena.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        lblContrasena.setText("Contrasena:");

        botonMostrar.setText("Mostrar");

        botonIngresar.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        botonIngresar.setText("Ingresar");
        botonIngresar.setFocusPainted(false);

        lblError.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblError.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(lblSubtitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(lblUsuario)
                    .addComponent(campoUsuario)
                    .addComponent(lblContrasena)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(campoContrasena, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(botonMostrar, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(botonIngresar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(115, 115, 115))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblImagen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblTitulo)
                .addGap(2, 2, 2)
                .addComponent(lblSubtitulo)
                .addGap(16, 16, 16)
                .addComponent(lblUsuario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(campoUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblContrasena)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(campoContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonMostrar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addComponent(botonIngresar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblError)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonIngresar;
    private javax.swing.JButton botonMostrar;
    private javax.swing.JPasswordField campoContrasena;
    private javax.swing.JTextField campoUsuario;
    private javax.swing.JLabel lblContrasena;
    private javax.swing.JLabel lblError;
    private javax.swing.JLabel lblImagen;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblUsuario;
    // End of variables declaration//GEN-END:variables
}
