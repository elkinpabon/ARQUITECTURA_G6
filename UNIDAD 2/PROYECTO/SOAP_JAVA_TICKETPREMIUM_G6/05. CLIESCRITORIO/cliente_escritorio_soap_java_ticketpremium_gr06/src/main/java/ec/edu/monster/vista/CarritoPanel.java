package ec.edu.monster.vista;

import ec.edu.monster.controlador.TicketController;
import ec.edu.monster.modelo.LineaCarrito;
import ec.edu.monster.util.Moneda;
import ec.edu.monster.ws.Factura;
import ec.edu.monster.ws.ItemCarrito;
import ec.edu.monster.ws.Resultado;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Pestana "Carrito": lineas reservadas + checkout CONTADO / CREDITO
 * (entrada, cuotas y tasa % mensual). Al pagar muestra la factura completa
 * con amortizacion y permite guardar el PDF.
 */
public class CarritoPanel extends JPanel {

    private final TicketController ctrl;
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Partido", "Seccion", "Categoria", "Fila-Asiento",
                         "Precio"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JLabel lblTotal = new JLabel("Total: $ 0.00");

    private final JRadioButton rbContado = new JRadioButton("Contado", true);
    private final JRadioButton rbCredito = new JRadioButton("Credito");
    private final JTextField txtEntrada = new JTextField("0", 8);
    private final JTextField txtCuotas = new JTextField("3", 5);
    private final JTextField txtTasa = new JTextField("2", 5);

    public CarritoPanel(TicketController ctrl) {
        this.ctrl = ctrl;
        setLayout(new BorderLayout(0, 10));
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new javax.swing.BoxLayout(titulos, javax.swing.BoxLayout.Y_AXIS));
        titulos.add(Estilo.titulo("Carrito de compra"));
        titulos.add(Estilo.subtitulo("Asientos reservados desde el mapa. Las reservas expiran si no pagas."));
        cab.add(titulos, BorderLayout.WEST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        JButton btnQuitar = Estilo.botonPlano("Quitar seleccionado");
        btnQuitar.addActionListener(e -> quitarSeleccionado());
        JButton btnVaciar = Estilo.botonPeligro("Vaciar carrito");
        btnVaciar.addActionListener(e -> vaciar());
        acciones.add(btnQuitar);
        acciones.add(btnVaciar);
        cab.add(acciones, BorderLayout.EAST);
        add(cab, BorderLayout.NORTH);

        Estilo.tabla(tabla);
        add(Estilo.scroll(tabla), BorderLayout.CENTER);
        add(construirCheckout(), BorderLayout.SOUTH);

        ctrl.getCarrito().onCambio(() ->
                SwingUtilities.invokeLater(this::refrescar));
        refrescar();
    }

    private JPanel construirCheckout() {
        JPanel panel = Estilo.tarjeta();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 8, 4, 8);
        g.anchor = GridBagConstraints.WEST;

        lblTotal.setFont(Estilo.fuente(Font.BOLD, 15));
        lblTotal.setForeground(Estilo.AZUL);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 6;
        panel.add(lblTotal, g);

        // Forma de pago
        rbContado.setOpaque(false);
        rbContado.setFont(Estilo.fuente(Font.BOLD, 12));
        rbCredito.setOpaque(false);
        rbCredito.setFont(Estilo.fuente(Font.BOLD, 12));
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbContado);
        grupo.add(rbCredito);
        rbContado.addActionListener(e -> actualizarCamposCredito());
        rbCredito.addActionListener(e -> actualizarCamposCredito());

        g.gridy = 1; g.gridwidth = 1;
        JLabel lblPago = new JLabel("Forma de pago:");
        lblPago.setFont(Estilo.fuente(Font.BOLD, 12));
        lblPago.setForeground(Estilo.TEXTO);
        panel.add(lblPago, g);
        g.gridx = 1; panel.add(rbContado, g);
        g.gridx = 2; panel.add(rbCredito, g);

        // Campos de credito
        g.gridx = 0; g.gridy = 2;
        panel.add(etiqueta("Entrada ($):"), g);
        g.gridx = 1; panel.add(txtEntrada, g);
        g.gridx = 2; panel.add(etiqueta("Numero de cuotas:"), g);
        g.gridx = 3; panel.add(txtCuotas, g);
        g.gridx = 4; panel.add(etiqueta("Tasa % mensual:"), g);
        g.gridx = 5; panel.add(txtTasa, g);

        JButton btnPagar = Estilo.botonExito("Pagar / Finalizar compra");
        g.gridx = 6; g.gridy = 2; g.anchor = GridBagConstraints.EAST;
        g.weightx = 1.0;
        btnPagar.addActionListener(e -> pagar());
        panel.add(btnPagar, g);

        actualizarCamposCredito();
        return panel;
    }

    private JLabel etiqueta(String t) {
        JLabel l = new JLabel(t);
        l.setFont(Estilo.fuente(Font.PLAIN, 12));
        l.setForeground(Estilo.TEXTO);
        return l;
    }

    private void actualizarCamposCredito() {
        boolean credito = rbCredito.isSelected();
        txtEntrada.setEnabled(credito);
        txtCuotas.setEnabled(credito);
        txtTasa.setEnabled(credito);
    }

    private void refrescar() {
        modelo.setRowCount(0);
        for (LineaCarrito l : ctrl.getCarrito().getLineas()) {
            modelo.addRow(new Object[]{l.getPartidoDesc(),
                    l.getSeccionLabel(), l.getCategoria(),
                    l.getFila() + "-" + l.getAsiento(),
                    Moneda.fmt(l.getPrecio())});
        }
        lblTotal.setText("Total: " + Moneda.fmt(ctrl.getCarrito().total())
                + "  (" + ctrl.getCarrito().size() + " boletos, antes de IVA)");
    }

    private void quitarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una linea del carrito.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int idx = tabla.convertRowIndexToModel(fila);
        LineaCarrito l = ctrl.getCarrito().get(idx);
        try {
            ctrl.liberarAsiento(l.getIdSeccion(), l.getFila(), l.getAsiento());
        } catch (Exception ignore) { }
        ctrl.getCarrito().quitar(idx);
    }

    private void vaciar() {
        if (ctrl.getCarrito().vacio()) return;
        if (JOptionPane.showConfirmDialog(this,
                "Se liberaran todas tus reservas. Continuar?",
                "Vaciar carrito", JOptionPane.YES_NO_OPTION)
                != JOptionPane.YES_OPTION) return;
        try {
            ctrl.liberarMisReservas();
        } catch (Exception ignore) { }
        ctrl.getCarrito().vaciar();
    }

    private void pagar() {
        if (ctrl.getCarrito().vacio()) {
            JOptionPane.showMessageDialog(this, "El carrito esta vacio.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String tipoPago = rbCredito.isSelected() ? "CREDITO" : "CONTADO";
        int numCuotas = 0;
        BigDecimal entrada = BigDecimal.ZERO;
        BigDecimal tasa = BigDecimal.ZERO;

        if (rbCredito.isSelected()) {
            try {
                entrada = numero(txtEntrada.getText());
                numCuotas = Integer.parseInt(txtCuotas.getText().trim());
                // tasa ingresada como % mensual (ej. 2) -> decimal (0.02)
                tasa = numero(txtTasa.getText())
                        .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Revisa los valores de entrada, cuotas y tasa.",
                        "Datos invalidos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (numCuotas <= 0) {
                JOptionPane.showMessageDialog(this,
                        "El numero de cuotas debe ser mayor a 0.",
                        "Datos invalidos", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        List<ItemCarrito> items = new ArrayList<>();
        for (LineaCarrito l : ctrl.getCarrito().getLineas()) {
            ItemCarrito it = new ItemCarrito();
            it.setCodigoPartido(l.getCodigoPartido());
            it.setIdSeccion(l.getIdSeccion());
            it.setCantidad(1);
            it.setFila(l.getFila());
            it.setAsientos(l.getAsiento());
            items.add(it);
        }

        try {
            Resultado r = ctrl.comprar(items, tipoPago, numCuotas, tasa, entrada);
            if (!r.isExito()) {
                JOptionPane.showMessageDialog(this, r.getMensaje(),
                        "Compra rechazada", JOptionPane.WARNING_MESSAGE);
                return;
            }
            ctrl.getCarrito().vaciar();

            Factura f = r.getFactura();
            if (f != null) {
                // recargar comprobante completo (detalles + amortizacion)
                try {
                    Factura completa = ctrl.comprobante(f.getIdFactura());
                    if (completa != null) f = completa;
                } catch (Exception ignore) { }
                FacturaDialog.mostrar(SwingUtilities.getWindowAncestor(this), f,
                        "Compra exitosa - Factura #" + f.getIdFactura());
            } else {
                JOptionPane.showMessageDialog(this, r.getMensaje(),
                        "Compra exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al finalizar la compra:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static BigDecimal numero(String v) {
        return (v == null || v.isBlank())
                ? BigDecimal.ZERO : new BigDecimal(v.trim().replace(",", "."));
    }
}
