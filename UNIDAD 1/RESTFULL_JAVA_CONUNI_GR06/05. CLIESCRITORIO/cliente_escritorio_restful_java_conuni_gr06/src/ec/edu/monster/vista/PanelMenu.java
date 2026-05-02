package ec.edu.monster.vista;

import java.awt.Color;
import java.awt.Image;
import java.net.URL;
import java.util.function.Consumer;
import javax.swing.ImageIcon;

/**
 * Panel del menu principal en formato Matisse (archivo .form emparejado).
 * Editable en NetBeans -> Design view.
 *
 * Cabecera arriba (logo + titulo + saludo + cerrar sesion) y 3 tarjetas
 * debajo (Longitud / Masa / Temperatura).
 */
public class PanelMenu extends javax.swing.JPanel {

    private Consumer<String> accionCategoria;
    private Runnable accionCerrarSesion;

    public PanelMenu() {
        initComponents();
        configurarVista();
        conectarEventos();
    }

    private void configurarVista() {
        setBackground(Paleta.GRIS_FONDO);

        // Cabecera: fondo azul con textos claros
        panelCabecera.setBackground(Paleta.AZUL);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(Paleta.ETIQUETA);
        lblSaludo.setForeground(Paleta.AMARILLO);
        lblSaludo.setFont(Paleta.SUBTITULO);

        URL urlLogo = getClass().getResource("/img/moster.png");
        if (urlLogo != null) {
            Image img = new ImageIcon(urlLogo).getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(img));
            lblLogo.setText("");
        }

        // Tarjetas: azul con texto blanco y opaco (para macOS)
        for (javax.swing.JButton tarjeta : new javax.swing.JButton[]{btnLongitud, btnMasa, btnTemperatura}) {
            tarjeta.setBackground(Paleta.AZUL);
            tarjeta.setForeground(Color.WHITE);
            tarjeta.setOpaque(true);
            tarjeta.setBorderPainted(false);
            tarjeta.setContentAreaFilled(true);
            tarjeta.setFocusPainted(false);
        }

        btnLongitud.setText("<html><div style='text-align:center;'>"
                + "<div style='font-size:18px;font-weight:bold;'>Longitud</div>"
                + "<div style='font-size:11px;color:#D0DAE8;margin-top:6px;'>"
                + "Metros, pies, millas, pulgadas...</div></div></html>");
        btnMasa.setText("<html><div style='text-align:center;'>"
                + "<div style='font-size:18px;font-weight:bold;'>Masa</div>"
                + "<div style='font-size:11px;color:#D0DAE8;margin-top:6px;'>"
                + "Kilogramos, libras, onzas...</div></div></html>");
        btnTemperatura.setText("<html><div style='text-align:center;'>"
                + "<div style='font-size:18px;font-weight:bold;'>Temperatura</div>"
                + "<div style='font-size:11px;color:#D0DAE8;margin-top:6px;'>"
                + "Celsius, Fahrenheit, Kelvin</div></div></html>");
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

    // ========= API publica consumida por ControladorEscritorio =========

    public void setUsuario(String usuario) {
        lblSaludo.setText("Bienvenido, " + usuario);
    }

    public void setOnCategoriaSeleccionada(Consumer<String> accion) {
        this.accionCategoria = accion;
    }

    public void setOnCerrarSesion(Runnable accion) {
        this.accionCerrarSesion = accion;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    @SuppressWarnings("unchecked")
    private void initComponents() {

        panelCabecera = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        lblSaludo = new javax.swing.JLabel();
        btnCerrarSesion = new javax.swing.JButton();
        btnLongitud = new javax.swing.JButton();
        btnMasa = new javax.swing.JButton();
        btnTemperatura = new javax.swing.JButton();

        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setText("Logo");

        lblTitulo.setText("Cliente Escritorio CONUNI");

        lblSaludo.setText("Bienvenido");

        btnCerrarSesion.setText("Cerrar Sesion");
        btnCerrarSesion.setFocusPainted(false);

        javax.swing.GroupLayout panelCabeceraLayout = new javax.swing.GroupLayout(panelCabecera);
        panelCabecera.setLayout(panelCabeceraLayout);
        panelCabeceraLayout.setHorizontalGroup(
            panelCabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCabeceraLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblSaludo)
                .addGap(18, 18, 18)
                .addComponent(btnCerrarSesion)
                .addContainerGap())
        );
        panelCabeceraLayout.setVerticalGroup(
            panelCabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCabeceraLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTitulo)
                    .addComponent(lblSaludo)
                    .addComponent(btnCerrarSesion))
                .addContainerGap())
        );

        btnLongitud.setText("Longitud");
        btnLongitud.setFocusPainted(false);

        btnMasa.setText("Masa");
        btnMasa.setFocusPainted(false);

        btnTemperatura.setText("Temperatura");
        btnTemperatura.setFocusPainted(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelCabecera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(btnLongitud, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addComponent(btnMasa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addComponent(btnTemperatura, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelCabecera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLongitud, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .addComponent(btnMasa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTemperatura, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnLongitud;
    private javax.swing.JButton btnMasa;
    private javax.swing.JButton btnTemperatura;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblSaludo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel panelCabecera;
    // End of variables declaration//GEN-END:variables
}
