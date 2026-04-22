package ec.edu.monster.vista;

import ec.edu.monster.modelo.Resultado;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import javax.swing.DefaultComboBoxModel;

/**
 * Panel de conversion en formato Matisse (archivo .form emparejado).
 * Editable en NetBeans -> Design view.
 *
 * Es parametrizable: llamar {@link #setCategoria(String)} con "longitud",
 * "masa" o "temperatura" cambia el titulo y las opciones del combo.
 */
public class PanelConversion extends javax.swing.JPanel {

    public static class Opcion {
        public final String clave;
        public final String etiqueta;
        public Opcion(String clave, String etiqueta) {
            this.clave = clave;
            this.etiqueta = etiqueta;
        }
        @Override public String toString() { return etiqueta; }
    }

    private static final List<Opcion> OPCIONES_LONGITUD = Arrays.asList(
        new Opcion("metrosAPies",          "Metros a Pies"),
        new Opcion("kilometrosAMillas",    "Kilometros a Millas"),
        new Opcion("centimetrosAPulgadas", "Centimetros a Pulgadas"),
        new Opcion("yardasAMetros",        "Yardas a Metros"),
        new Opcion("milimetrosAPulgadas",  "Milimetros a Pulgadas")
    );
    private static final List<Opcion> OPCIONES_MASA = Arrays.asList(
        new Opcion("kilogramosALibras",    "Kilogramos a Libras"),
        new Opcion("gramosAOnzas",         "Gramos a Onzas"),
        new Opcion("toneladasAKilogramos", "Toneladas a Kilogramos"),
        new Opcion("librasAOnzas",         "Libras a Onzas"),
        new Opcion("miligramosAGramos",    "Miligramos a Gramos")
    );
    private static final List<Opcion> OPCIONES_TEMPERATURA = Arrays.asList(
        new Opcion("celsiusAFahrenheit", "Celsius a Fahrenheit"),
        new Opcion("fahrenheitACelsius", "Fahrenheit a Celsius"),
        new Opcion("celsiusAKelvin",     "Celsius a Kelvin"),
        new Opcion("kelvinACelsius",     "Kelvin a Celsius"),
        new Opcion("fahrenheitAKelvin",  "Fahrenheit a Kelvin")
    );

    private String categoriaActual;
    private BiConsumer<String, Double> accionConvertir;
    private Runnable accionVolver;

    public PanelConversion() {
        initComponents();
        configurarVista();
        conectarEventos();
    }

    private void configurarVista() {
        setBackground(Paleta.GRIS_FONDO);

        lblEncabezado.setForeground(Paleta.AZUL);
        lblEncabezado.setFont(Paleta.TITULO);

        btnConvertir.setBackground(Paleta.AZUL);
        btnConvertir.setForeground(Color.WHITE);
        btnConvertir.setOpaque(true);
        btnConvertir.setBorderPainted(false);
        btnConvertir.setContentAreaFilled(true);

        lblResultado.setOpaque(true);
        lblResultado.setBackground(Color.WHITE);
    }

    private void conectarEventos() {
        btnConvertir.addActionListener(e -> dispararConvertir());
        campoValor.addActionListener(e -> dispararConvertir());
        btnVolver.addActionListener(e -> {
            if (accionVolver != null) accionVolver.run();
        });
    }

    private void dispararConvertir() {
        Opcion seleccion = (Opcion) comboOperacion.getSelectedItem();
        if (seleccion == null) return;
        String texto = campoValor.getText().trim().replace(',', '.');
        try {
            double valor = Double.parseDouble(texto);
            if (accionConvertir != null) accionConvertir.accept(seleccion.clave, valor);
        } catch (NumberFormatException ex) {
            mostrarResultado(Resultado.error("Ingresa un numero valido (ej: 12.5)"));
        }
    }

    // ========= API publica consumida por ControladorEscritorio =========

    @SuppressWarnings("unchecked")
    public void setCategoria(String categoria) {
        this.categoriaActual = categoria;
        List<Opcion> opciones;
        String titulo;
        switch (categoria) {
            case "longitud":
                opciones = OPCIONES_LONGITUD;
                titulo = "Conversiones de Longitud";
                break;
            case "masa":
                opciones = OPCIONES_MASA;
                titulo = "Conversiones de Masa";
                break;
            case "temperatura":
                opciones = OPCIONES_TEMPERATURA;
                titulo = "Conversiones de Temperatura";
                break;
            default:
                opciones = List.of();
                titulo = "Conversiones";
        }
        lblEncabezado.setText(titulo);
        comboOperacion.setModel(new DefaultComboBoxModel(opciones.toArray()));
        campoValor.setText("");
        lblResultado.setText(" ");
        lblResultado.setBackground(Color.WHITE);
    }

    public String getCategoria() {
        return categoriaActual;
    }

    public void mostrarResultado(Resultado resultado) {
        if (resultado == null) return;
        if (resultado.isExito()) {
            lblResultado.setText("Resultado: " + resultado.getValor());
            lblResultado.setBackground(Paleta.VERDE_EXITO_BG);
            lblResultado.setForeground(Paleta.VERDE_EXITO_FG);
        } else {
            lblResultado.setText(resultado.getMensaje());
            lblResultado.setBackground(Paleta.ROJO_ERROR_BG);
            lblResultado.setForeground(Paleta.ROJO_ERROR_FG);
        }
    }

    public void setBotonHabilitado(boolean habilitado) {
        btnConvertir.setEnabled(habilitado);
        btnConvertir.setText(habilitado ? "Convertir" : "Convirtiendo...");
    }

    public void setOnConvertir(BiConsumer<String, Double> accion) {
        this.accionConvertir = accion;
    }

    public void setOnVolver(Runnable accion) {
        this.accionVolver = accion;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    @SuppressWarnings("unchecked")
    private void initComponents() {

        lblEncabezado = new javax.swing.JLabel();
        lblConversion = new javax.swing.JLabel();
        comboOperacion = new javax.swing.JComboBox<>();
        lblValor = new javax.swing.JLabel();
        campoValor = new javax.swing.JTextField();
        btnConvertir = new javax.swing.JButton();
        lblResultado = new javax.swing.JLabel();
        btnVolver = new javax.swing.JButton();

        lblEncabezado.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        lblEncabezado.setText("Conversiones");

        lblConversion.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        lblConversion.setText("Conversion:");

        lblValor.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        lblValor.setText("Valor:");

        btnConvertir.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        btnConvertir.setText("Convertir");
        btnConvertir.setFocusPainted(false);

        lblResultado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblResultado.setText(" ");
        lblResultado.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 12, 10, 12));

        btnVolver.setText("Volver al Menu");
        btnVolver.setFocusPainted(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEncabezado)
                    .addComponent(lblConversion)
                    .addComponent(comboOperacion, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblValor)
                    .addComponent(campoValor)
                    .addComponent(btnConvertir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblResultado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnVolver, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(40, 40, 40))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lblEncabezado)
                .addGap(16, 16, 16)
                .addComponent(lblConversion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboOperacion, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblValor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(campoValor, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(btnConvertir, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblResultado, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConvertir;
    private javax.swing.JButton btnVolver;
    private javax.swing.JTextField campoValor;
    private javax.swing.JComboBox<Object> comboOperacion;
    private javax.swing.JLabel lblConversion;
    private javax.swing.JLabel lblEncabezado;
    private javax.swing.JLabel lblResultado;
    private javax.swing.JLabel lblValor;
    // End of variables declaration//GEN-END:variables
}
