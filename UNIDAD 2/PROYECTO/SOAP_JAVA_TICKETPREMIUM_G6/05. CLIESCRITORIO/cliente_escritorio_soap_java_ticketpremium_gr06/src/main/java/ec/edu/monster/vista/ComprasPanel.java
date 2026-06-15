package ec.edu.monster.vista;

import ec.edu.monster.controlador.TicketController;
import ec.edu.monster.util.Moneda;
import ec.edu.monster.ws.Factura;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Pestana "Mis compras": historial de facturas del usuario; al seleccionar
 * una se muestra el detalle (boletos) y la amortizacion si fue a CREDITO,
 * con boton para guardar el comprobante PDF.
 */
public class ComprasPanel extends JPanel {

    private final TicketController ctrl;
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Factura", "Fecha", "Tipo pago", "Subtotal", "IVA",
                         "Total"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final List<Factura> facturas = new ArrayList<>();
    private final FacturaDetallePanel detalle = new FacturaDetallePanel();

    public ComprasPanel(TicketController ctrl) {
        this.ctrl = ctrl;
        setLayout(new BorderLayout(0, 10));
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new javax.swing.BoxLayout(titulos, javax.swing.BoxLayout.Y_AXIS));
        titulos.add(Estilo.titulo("Mis compras"));
        titulos.add(Estilo.subtitulo("Selecciona una factura para ver sus boletos y la amortizacion (si fue a credito)."));
        cab.add(titulos, BorderLayout.WEST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        JButton btnRefrescar = Estilo.botonPlano("Actualizar");
        btnRefrescar.addActionListener(e -> cargar());
        acciones.add(btnRefrescar);
        cab.add(acciones, BorderLayout.EAST);
        add(cab, BorderLayout.NORTH);

        Estilo.tabla(tabla);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarSeleccion();
        });

        detalle.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                Estilo.scroll(tabla), detalle);
        split.setResizeWeight(0.38);
        split.setBorder(null);
        split.setBackground(Estilo.FONDO);
        add(split, BorderLayout.CENTER);

        cargar();
    }

    public final void cargar() {
        try {
            facturas.clear();
            facturas.addAll(ctrl.misFacturas());
            modelo.setRowCount(0);
            for (Factura f : facturas) {
                modelo.addRow(new Object[]{f.getIdFactura(), nn(f.getFecha()),
                        nn(f.getTipoPago()), Moneda.fmt(f.getSubtotal()),
                        Moneda.fmt(f.getIva()), Moneda.fmt(f.getTotal())});
            }
            detalle.setFactura(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar las facturas:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            detalle.setFactura(null);
            return;
        }
        int idx = tabla.convertRowIndexToModel(fila);
        Factura f = facturas.get(idx);
        try {
            // comprobante completo: detalles + amortizacion
            Factura completa = ctrl.comprobante(f.getIdFactura());
            detalle.setFactura(completa != null ? completa : f);
        } catch (Exception ex) {
            detalle.setFactura(f);
        }
    }

    private static String nn(String s) { return s == null ? "" : s; }
}
