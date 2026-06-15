package ec.edu.monster.vista;

import ec.edu.monster.ws.Factura;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import javax.swing.JDialog;

/** Dialogo modal que muestra una factura completa (detalle + amortizacion). */
public class FacturaDialog extends JDialog {

    public FacturaDialog(Window owner, Factura factura, String titulo) {
        super(owner, titulo, ModalityType.APPLICATION_MODAL);
        FacturaDetallePanel panel = new FacturaDetallePanel();
        panel.setFactura(factura);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        setSize(new Dimension(820, 560));
        setLocationRelativeTo(owner);
    }

    public static void mostrar(Window owner, Factura factura, String titulo) {
        new FacturaDialog(owner, factura, titulo).setVisible(true);
    }
}
