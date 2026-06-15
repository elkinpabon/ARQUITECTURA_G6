package ec.edu.monster.vista;

import ec.edu.monster.controlador.TicketController;
import ec.edu.monster.util.Moneda;
import ec.edu.monster.ws.Cuenta;
import ec.edu.monster.ws.Movimiento;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Pestana "Mi cuenta": numero de cuenta, saldo y movimientos
 * (debitos de compras a contado y de cuotas de credito).
 */
public class CuentaPanel extends JPanel {

    private final TicketController ctrl;
    private final JLabel lblNumero = new JLabel("-");
    private final JLabel lblSaldo = new JLabel("-");
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Fecha", "Tipo", "Monto", "Descripcion", "Factura"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public CuentaPanel(TicketController ctrl) {
        this.ctrl = ctrl;
        setLayout(new BorderLayout(0, 10));
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new javax.swing.BoxLayout(titulos, javax.swing.BoxLayout.Y_AXIS));
        titulos.add(Estilo.titulo("Mi cuenta"));
        titulos.add(Estilo.subtitulo("Cuenta bancaria simulada usada para pagar las compras."));
        cab.add(titulos, BorderLayout.WEST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        JButton btnRefrescar = Estilo.botonPlano("Actualizar");
        btnRefrescar.addActionListener(e -> cargar());
        acciones.add(btnRefrescar);
        cab.add(acciones, BorderLayout.EAST);
        add(cab, BorderLayout.NORTH);

        // Resumen de cuenta
        JPanel resumen = Estilo.tarjeta();
        resumen.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 4));
        JLabel l1 = new JLabel("Numero de cuenta:");
        l1.setFont(Estilo.fuente(Font.BOLD, 13));
        l1.setForeground(Estilo.TEXTO);
        lblNumero.setFont(Estilo.fuente(Font.PLAIN, 13));
        lblNumero.setForeground(Estilo.TEXTO);
        JLabel l2 = new JLabel("Saldo disponible:");
        l2.setFont(Estilo.fuente(Font.BOLD, 13));
        l2.setForeground(Estilo.TEXTO);
        lblSaldo.setFont(Estilo.fuente(Font.BOLD, 15));
        lblSaldo.setForeground(Estilo.EXITO);
        resumen.add(l1);
        resumen.add(lblNumero);
        resumen.add(l2);
        resumen.add(lblSaldo);

        JTable tabla = new JTable(modelo);
        Estilo.tabla(tabla);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(resumen, BorderLayout.NORTH);
        centro.add(Estilo.scroll(tabla), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        cargar();
    }

    public final void cargar() {
        try {
            Cuenta c = ctrl.miCuenta();
            if (c != null) {
                lblNumero.setText(c.getNumero() == null ? "-" : c.getNumero());
                lblSaldo.setText(Moneda.fmt(c.getSaldo()));
            }
            modelo.setRowCount(0);
            for (Movimiento m : ctrl.misMovimientos()) {
                modelo.addRow(new Object[]{nn(m.getFecha()), nn(m.getTipo()),
                        Moneda.fmt(m.getMonto()), nn(m.getDescripcion()),
                        m.getIdFactura() > 0 ? "#" + m.getIdFactura() : ""});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la cuenta:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String nn(String s) { return s == null ? "" : s; }
}
