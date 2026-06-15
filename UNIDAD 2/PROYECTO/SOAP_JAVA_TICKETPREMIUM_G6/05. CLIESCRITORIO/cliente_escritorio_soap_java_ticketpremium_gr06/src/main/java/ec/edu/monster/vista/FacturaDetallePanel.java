package ec.edu.monster.vista;

import ec.edu.monster.servicio.GeneradorComprobantePDF;
import ec.edu.monster.util.Moneda;
import ec.edu.monster.ws.Cuota;
import ec.edu.monster.ws.DetalleFactura;
import ec.edu.monster.ws.Factura;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Panel reutilizable que muestra una factura completa: cabecera, detalle de
 * boletos y (si fue a CREDITO) la tabla de amortizacion. Incluye el boton
 * "Guardar PDF" que usa el generador PDFBox.
 */
public class FacturaDetallePanel extends JPanel {

    private final JLabel lblCabecera = new JLabel(" ");
    private final JLabel lblTotales = new JLabel(" ");
    private final JLabel lblCredito = new JLabel(" ");
    private final DefaultTableModel modeloDetalles = new DefaultTableModel(
            new Object[]{"Partido", "Categoria", "Fila", "Asientos", "Cant.",
                         "P. unitario", "Total"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final DefaultTableModel modeloAmort = new DefaultTableModel(
            new Object[]{"Cuota", "Vencimiento", "Valor cuota", "Interes",
                         "Abono capital", "Saldo final"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JScrollPane scrollAmort;
    private final JLabel lblAmortTitulo = new JLabel("Tabla de amortizacion");
    private final JButton btnPdf = Estilo.botonPrimario("Guardar PDF");

    private Factura factura;

    public FacturaDetallePanel() {
        setLayout(new BorderLayout(0, 8));
        setBackground(Estilo.SUPERFICIE);
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        lblCabecera.setFont(Estilo.fuente(Font.BOLD, 14));
        lblCabecera.setForeground(Estilo.TEXTO);
        lblTotales.setFont(Estilo.fuente(Font.PLAIN, 12));
        lblTotales.setForeground(Estilo.TEXTO);
        lblCredito.setFont(Estilo.fuente(Font.PLAIN, 12));
        lblCredito.setForeground(Estilo.AZUL);

        JPanel norte = new JPanel();
        norte.setOpaque(false);
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.add(lblCabecera);
        norte.add(Box.createVerticalStrut(4));
        norte.add(lblTotales);
        norte.add(lblCredito);
        add(norte, BorderLayout.NORTH);

        JTable tDet = new JTable(modeloDetalles);
        Estilo.tabla(tDet);
        JTable tAmort = new JTable(modeloAmort);
        Estilo.tabla(tAmort);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        JLabel lblDet = new JLabel("Boletos");
        lblDet.setFont(Estilo.fuente(Font.BOLD, 12));
        lblDet.setForeground(Estilo.TEXTO);
        lblDet.setAlignmentX(LEFT_ALIGNMENT);
        centro.add(lblDet);
        centro.add(Box.createVerticalStrut(4));
        JScrollPane spDet = Estilo.scroll(tDet);
        spDet.setAlignmentX(LEFT_ALIGNMENT);
        spDet.setPreferredSize(new java.awt.Dimension(10, 140));
        centro.add(spDet);
        centro.add(Box.createVerticalStrut(10));

        lblAmortTitulo.setFont(Estilo.fuente(Font.BOLD, 12));
        lblAmortTitulo.setForeground(Estilo.TEXTO);
        lblAmortTitulo.setAlignmentX(LEFT_ALIGNMENT);
        centro.add(lblAmortTitulo);
        centro.add(Box.createVerticalStrut(4));
        scrollAmort = Estilo.scroll(tAmort);
        scrollAmort.setAlignmentX(LEFT_ALIGNMENT);
        scrollAmort.setPreferredSize(new java.awt.Dimension(10, 150));
        centro.add(scrollAmort);
        add(centro, BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        sur.setOpaque(false);
        btnPdf.addActionListener(e -> guardarPdf());
        sur.add(btnPdf);
        add(sur, BorderLayout.SOUTH);

        setFactura(null);
    }

    /** Carga la factura (puede ser null para limpiar). */
    public final void setFactura(Factura f) {
        this.factura = f;
        modeloDetalles.setRowCount(0);
        modeloAmort.setRowCount(0);

        if (f == null) {
            lblCabecera.setText("Sin factura seleccionada");
            lblTotales.setText(" ");
            lblCredito.setText(" ");
            lblAmortTitulo.setVisible(false);
            scrollAmort.setVisible(false);
            btnPdf.setEnabled(false);
            return;
        }
        btnPdf.setEnabled(true);

        lblCabecera.setText("Factura #" + f.getIdFactura()
                + "   |   " + nn(f.getFecha())
                + "   |   Cliente: " + nn(f.getUsuarioNombre())
                + "   |   Pago: " + nn(f.getTipoPago()));
        lblTotales.setText("Subtotal: " + Moneda.fmt(f.getSubtotal())
                + "    IVA: " + Moneda.fmt(f.getIva())
                + "    TOTAL: " + Moneda.fmt(f.getTotal()));

        boolean credito = "CREDITO".equalsIgnoreCase(nn(f.getTipoPago()));
        if (credito) {
            lblCredito.setText("Credito - Entrada: " + Moneda.fmt(f.getEntrada())
                    + "    Financiado: " + Moneda.fmt(f.getMontoFinanciado())
                    + "    Cuotas: " + f.getNumCuotas()
                    + "    Tasa mensual: " + tasaPct(f.getTasaInteres()));
        } else {
            lblCredito.setText("Pago a CONTADO (debitado de la cuenta).");
        }

        if (f.getDetalles() != null) {
            for (DetalleFactura d : f.getDetalles()) {
                modeloDetalles.addRow(new Object[]{
                        nn(d.getDescripcionPartido()), nn(d.getCategoria()),
                        nn(d.getFila()), nn(d.getAsientos()), d.getCantidad(),
                        Moneda.fmt(d.getPrecioUnitario()), Moneda.fmt(d.getTotal())});
            }
        }

        boolean hayAmort = credito && f.getAmortizacion() != null
                && !f.getAmortizacion().isEmpty();
        lblAmortTitulo.setVisible(hayAmort);
        scrollAmort.setVisible(hayAmort);
        if (hayAmort) {
            for (Cuota c : f.getAmortizacion()) {
                modeloAmort.addRow(new Object[]{
                        c.getNumCuota(), nn(c.getFechaVencimiento()),
                        Moneda.fmt(c.getCuota()), Moneda.fmt(c.getInteres()),
                        Moneda.fmt(c.getAbonoCapital()), Moneda.fmt(c.getSaldoFinal())});
            }
        }
        revalidate();
        repaint();
    }

    private void guardarPdf() {
        if (factura == null) return;
        try {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Guardar comprobante PDF");
            fc.setSelectedFile(new File("comprobante_factura_"
                    + factura.getIdFactura() + ".pdf"));
            if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
            File destino = fc.getSelectedFile();
            if (!destino.getName().toLowerCase().endsWith(".pdf")) {
                destino = new File(destino.getParentFile(), destino.getName() + ".pdf");
            }
            byte[] pdf = GeneradorComprobantePDF.comprobante(factura);
            Files.write(destino.toPath(), pdf);
            JOptionPane.showMessageDialog(this,
                    "Comprobante guardado en:\n" + destino.getAbsolutePath(),
                    "PDF generado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo generar el PDF:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String nn(String s) { return s == null ? "" : s; }

    private static String tasaPct(BigDecimal tasa) {
        if (tasa == null) return "0.00 %";
        return String.format("%.2f %%", tasa.multiply(BigDecimal.valueOf(100)));
    }
}
