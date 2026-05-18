package ec.edu.monster.vista;

import ec.edu.monster.modelo.Resultado;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Panel parametrizable para las 3 categorias de conversion. El mismo panel se
 * re-configura al entrar a Longitud, Masa o Temperatura via {@link #setCategoria}.
 */
public class PanelConversion extends JPanel {

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

    private final JLabel lblEncabezado = new JLabel();
    private final JComboBox<Opcion> comboOperacion = new JComboBox<>();
    private final JTextField campoValor = new JTextField();
    private final JButton btnConvertir = new JButton("Convertir");
    private final JButton btnVolver = new JButton("Volver al Menu");
    private final JLabel lblResultado = new JLabel(" ");

    private String categoriaActual;
    private BiConsumer<String, Double> accionConvertir;
    private Runnable accionVolver;

    public PanelConversion() {
        setLayout(new BorderLayout());
        setBackground(Paleta.GRIS_FONDO);
        setBorder(BorderFactory.createEmptyBorder(24, 40, 24, 40));

        add(construirFormulario(), BorderLayout.CENTER);
        conectarEventos();
    }

    private JPanel construirFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD0, 0xD6, 0xDF), 1),
            BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 0, 6, 0);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;

        lblEncabezado.setFont(Paleta.TITULO);
        lblEncabezado.setForeground(Paleta.AZUL);
        panel.add(lblEncabezado, c);

        c.gridy++;
        JLabel lblOp = new JLabel("Conversion:");
        lblOp.setFont(Paleta.ETIQUETA);
        panel.add(lblOp, c);

        c.gridy++;
        comboOperacion.setFont(Paleta.CAMPO);
        comboOperacion.setPreferredSize(new Dimension(300, 30));
        panel.add(comboOperacion, c);

        c.gridy++;
        JLabel lblVal = new JLabel("Valor:");
        lblVal.setFont(Paleta.ETIQUETA);
        panel.add(lblVal, c);

        c.gridy++;
        campoValor.setFont(Paleta.CAMPO);
        campoValor.setPreferredSize(new Dimension(300, 30));
        panel.add(campoValor, c);

        c.gridy++;
        btnConvertir.setBackground(Paleta.AZUL);
        btnConvertir.setForeground(Color.WHITE);
        btnConvertir.setFont(Paleta.ETIQUETA);
        btnConvertir.setFocusPainted(false);
        btnConvertir.setOpaque(true);
        btnConvertir.setBorderPainted(false);
        btnConvertir.setPreferredSize(new Dimension(300, 36));
        panel.add(btnConvertir, c);

        c.gridy++;
        lblResultado.setOpaque(true);
        lblResultado.setFont(Paleta.ETIQUETA);
        lblResultado.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        lblResultado.setBackground(Color.WHITE);
        lblResultado.setForeground(Color.BLACK);
        lblResultado.setPreferredSize(new Dimension(300, 40));
        panel.add(lblResultado, c);

        c.gridy++;
        btnVolver.setFocusPainted(false);
        btnVolver.setPreferredSize(new Dimension(300, 30));
        panel.add(btnVolver, c);

        return panel;
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

    /** Re-configura el panel con las opciones y encabezado de la categoria indicada. */
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
        comboOperacion.setModel(new DefaultComboBoxModel<>(opciones.toArray(new Opcion[0])));
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
}
