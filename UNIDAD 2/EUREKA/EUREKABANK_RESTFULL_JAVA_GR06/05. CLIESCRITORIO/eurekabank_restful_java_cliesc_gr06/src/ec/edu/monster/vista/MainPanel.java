package ec.edu.monster.vista;

import ec.edu.monster.controlador.BancoController;
import ec.edu.monster.util.Moneda;
import ec.edu.monster.ws.ClienteResumen;
import ec.edu.monster.ws.CuentaResumen;
import ec.edu.monster.ws.MovimientoModel;
import ec.edu.monster.ws.Resultado;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Vista principal Swing — mismas operaciones que el cliente web. */
public class MainPanel extends JPanel {

    private static final Set<String> INGRESOS = Set.of("001", "003", "005", "008");

    private final BancoController ctrl;
    private final JComboBox<String> cboClientes = new JComboBox<>();
    private final JComboBox<String> cboCuenta = new JComboBox<>();
    private final JTextField txtMonto = new JTextField(10);
    private final JComboBox<String> cboMoneda =
            new JComboBox<>(new String[]{"Dólares (preferente)", "Soles"});
    private final DefaultTableModel modelo =
            new DefaultTableModel(new Object[]{"Cuenta", "Saldo", "Moneda", "Estado"}, 0);
    private final JLabel lblInfo = new JLabel(" ");
    private final JLabel lblTotal = new JLabel("Saldo total: 0.00");

    public MainPanel(BancoController ctrl, Runnable onLogout) {
        this.ctrl = ctrl;
        setLayout(new BorderLayout(8, 8));

        boolean admin = ctrl.getSesion().isAdmin();

        // ---- Norte: identidad + acciones ----
        JPanel north = new JPanel(new BorderLayout());
        lblInfo.setText("  Usuario: " + ctrl.getSesion().getUsuario()
                + (admin ? "  [ADMIN]" : ""));
        JPanel oeste = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        oeste.add(Img.label("/images/moster.png", 40));
        oeste.add(lblInfo);
        north.add(oeste, BorderLayout.WEST);
        JPanel northBtns = new JPanel(new WrapLayout(FlowLayout.RIGHT));
        JButton btnRefrescar = new JButton("Actualizar saldos");
        JButton btnSalir = new JButton("Cerrar sesión");
        northBtns.add(btnRefrescar);
        northBtns.add(btnSalir);
        north.add(northBtns, BorderLayout.EAST);
        add(north, BorderLayout.NORTH);

        // ---- Centro: (admin) buscar cliente + tabla cuentas ----
        JPanel center = new JPanel(new BorderLayout(6, 6));
        if (admin) {
            JPanel buscar = new JPanel(new WrapLayout(FlowLayout.LEFT));
            buscar.add(new JLabel("Cliente:"));
            buscar.add(cboClientes);
            JButton btnVer = new JButton("Ver cuentas");
            buscar.add(btnVer);
            JButton btnRegCli = new JButton("Registrar cliente");
            JButton btnRegCta = new JButton("Registrar cuenta");
            JButton btnDelCta = new JButton("Eliminar cuenta");
            buscar.add(btnRegCli);
            buscar.add(btnRegCta);
            buscar.add(btnDelCta);
            center.add(buscar, BorderLayout.NORTH);
            cargarComboClientes();
            btnVer.addActionListener(e -> {
                String sel = (String) cboClientes.getSelectedItem();
                if (sel != null) {
                    ctrl.cargarCuentas(sel.split(" ")[0]);
                    refrescarTabla();
                }
            });
            btnRegCli.addActionListener(e -> dialogoRegistrarCliente());
            btnRegCta.addActionListener(e -> dialogoRegistrarCuenta());
            btnDelCta.addActionListener(e -> dialogoEliminarCuenta());
        }

        JTable tabla = new JTable(modelo);
        center.add(new JScrollPane(tabla), BorderLayout.CENTER);
        center.add(lblTotal, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        // ---- Sur: operar ----
        JPanel sur = new JPanel(new GridLayout(0, 1, 4, 4));
        JPanel fila1 = new JPanel(new WrapLayout(FlowLayout.LEFT));
        fila1.add(new JLabel("Cuenta:"));
        fila1.add(cboCuenta);
        fila1.add(new JLabel("Monto:"));
        fila1.add(txtMonto);
        fila1.add(new JLabel("Moneda:"));
        fila1.add(cboMoneda);
        sur.add(fila1);

        JPanel fila2 = new JPanel(new WrapLayout(FlowLayout.LEFT));
        JButton btnSaldo = new JButton("Consultar saldo");
        JButton btnDep = new JButton("Depositar");
        JButton btnRet = new JButton("Retirar");
        JButton btnTrans = new JButton("Transferir");
        JButton btnMov = new JButton("Ver movimientos");
        btnSaldo.addActionListener(e -> accionSaldo());
        btnDep.addActionListener(e -> accionDeposito());
        btnRet.addActionListener(e -> accionRetiro());
        btnTrans.addActionListener(e -> accionTransferir());
        btnMov.addActionListener(e -> accionMovimientos());
        fila2.add(btnSaldo);
        if (admin) fila2.add(btnDep);   // depósito solo admin
        fila2.add(btnRet);
        fila2.add(btnTrans);
        fila2.add(btnMov);
        sur.add(fila2);
        add(sur, BorderLayout.SOUTH);

        btnRefrescar.addActionListener(e -> {
            ctrl.cargarCuentas(clienteActual());
            refrescarTabla();
        });
        btnSalir.addActionListener(e -> onLogout.run());

        refrescarTabla(); // cliente: ya cargó sus cuentas en login
    }

    /* ---------- helpers ---------- */

    private String monedaSel() {
        return cboMoneda.getSelectedIndex() == 1 ? "01" : "02";
    }

    private String clienteActual() {
        List<CuentaResumen> ct = ctrl.getCuentas();
        if (ct != null && !ct.isEmpty()) return ct.get(0).getCodigoCliente();
        return null;
    }

    private void cargarComboClientes() {
        try {
            cboClientes.removeAllItems();
            for (ClienteResumen c : ctrl.listarClientes()) {
                cboClientes.addItem(c.getCodigo() + "  ·  DNI " + c.getDni()
                        + "  ·  " + c.getNombre());
            }
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void refrescarTabla() {
        modelo.setRowCount(0);
        cboCuenta.removeAllItems();
        List<CuentaResumen> ct = ctrl.getCuentas();
        double total = 0;
        if (ct != null) {
            for (CuentaResumen c : ct) {
                modelo.addRow(new Object[]{c.getCodigoCuenta(),
                        String.format("%,.2f", c.getSaldo()),
                        Moneda.nombre(c.getMoneda()), c.getEstado()});
                cboCuenta.addItem(c.getCodigoCuenta());
                total += c.getSaldo();
            }
        }
        lblTotal.setText("Saldo total: " + String.format("%,.2f", total));
    }

    private String cuentaSel() {
        return (String) cboCuenta.getSelectedItem();
    }

    private void result(Resultado r) {
        JOptionPane.showMessageDialog(this, r.getMensaje(),
                r.isExito() ? "OK" : "Error",
                r.isExito() ? JOptionPane.INFORMATION_MESSAGE
                            : JOptionPane.ERROR_MESSAGE);
        ctrl.cargarCuentas(clienteActual());
        refrescarTabla();
    }

    private void error(String m) {
        JOptionPane.showMessageDialog(this, m, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    /* ---------- acciones ---------- */

    private void accionSaldo() {
        if (cuentaSel() == null) return;
        try { result(ctrl.consultarSaldo(cuentaSel())); }
        catch (Exception e) { error(e.getMessage()); }
    }

    private void accionDeposito() {
        if (cuentaSel() == null) return;
        try { result(ctrl.depositar(cuentaSel(), txtMonto.getText().trim(), monedaSel())); }
        catch (Exception e) { error(e.getMessage()); }
    }

    private void accionRetiro() {
        if (cuentaSel() == null) return;
        try { result(ctrl.retirar(cuentaSel(), txtMonto.getText().trim(), monedaSel())); }
        catch (Exception e) { error(e.getMessage()); }
    }

    private void accionTransferir() {
        if (cuentaSel() == null) return;
        String dst = JOptionPane.showInputDialog(this,
                "Cuenta destino (de cualquier cliente):");
        if (dst == null || dst.isBlank()) return;
        try {
            result(ctrl.transferir(cuentaSel(), dst.trim(),
                    txtMonto.getText().trim(), monedaSel()));
        } catch (Exception e) { error(e.getMessage()); }
    }

    private void accionMovimientos() {
        if (cuentaSel() == null) return;
        try {
            List<MovimientoModel> ms = ctrl.movimientos(cuentaSel());
            DefaultTableModel mm = new DefaultTableModel(new Object[]{
                    "#", "Fecha", "Tipo", "Mov.", "Crédito", "Débito",
                    "Cta.Ref.", "Conversión"}, 0);
            double tin = 0, tout = 0;
            for (MovimientoModel m : ms) {
                boolean in = INGRESOS.contains(m.getCodigoTipoMovimiento());
                double imp = m.getImporteMovimiento();
                if (in) tin += imp; else tout += imp;
                String conv = "";
                if (m.getMonedaOrigen() != null && !m.getMonedaOrigen().isBlank()) {
                    conv = String.format("%,.2f %s × %s = %,.2f",
                            m.getImporteOrigen() == null ? 0 : m.getImporteOrigen(),
                            Moneda.nombre(m.getMonedaOrigen()),
                            m.getTasaAplicada(), imp);
                }
                mm.addRow(new Object[]{m.getNumeroMovimiento(),
                        m.getFechaMovimiento(), m.getTipoDescripcion(),
                        in ? "CRÉDITO" : "DÉBITO",
                        in ? String.format("%,.2f", imp) : "",
                        in ? "" : String.format("%,.2f", imp),
                        m.getCuentaReferencia() == null ? "-" : m.getCuentaReferencia(),
                        conv});
            }
            final JTable t = new JTable(mm);
            JScrollPane sp = new JScrollPane(t);
            sp.setPreferredSize(new java.awt.Dimension(820, 360));

            final String cta = cuentaSel();
            final java.text.MessageFormat header = new java.text.MessageFormat(
                    "EUREKABANK GR06 - Banca RESTFULL - Estado de cuenta " + cta);
            final java.text.MessageFormat footer = new java.text.MessageFormat(
                    "Pagina {0}  -  Ingresos " + String.format("%,.2f", tin)
                    + "  Egresos " + String.format("%,.2f", tout));

            javax.swing.JDialog dlg = new javax.swing.JDialog(
                    (java.awt.Frame) null, "Movimientos " + cta, true);
            dlg.setLayout(new BorderLayout(8, 8));
            dlg.add(sp, BorderLayout.CENTER);
            JPanel acc = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnPdf = new JButton("Imprimir / Guardar PDF");
            JButton btnClose = new JButton("Cerrar");
            acc.add(btnPdf);
            acc.add(btnClose);
            dlg.add(acc, BorderLayout.SOUTH);
            btnPdf.addActionListener(ev -> {
                try {
                    // Diálogo de impresión del SO: elegir "Guardar como PDF".
                    t.print(JTable.PrintMode.FIT_WIDTH, header, footer);
                } catch (java.awt.print.PrinterException pe) {
                    error("No se pudo imprimir/exportar: " + pe.getMessage());
                }
            });
            btnClose.addActionListener(ev -> dlg.dispose());
            dlg.pack();
            dlg.setLocationRelativeTo(this);
            dlg.setVisible(true);
        } catch (Exception e) { error(e.getMessage()); }
    }

    /* ---------- diálogos admin ---------- */

    private void dialogoRegistrarCliente() {
        JTextField pat = new JTextField(), mat = new JTextField(),
                nom = new JTextField(), dni = new JTextField(),
                ciu = new JTextField("QUITO"), dir = new JTextField(),
                tel = new JTextField(), ema = new JTextField();
        JPanel p = new JPanel(new GridLayout(0, 2, 4, 4));
        p.add(new JLabel("Nombre")); p.add(nom);
        p.add(new JLabel("Ap. paterno")); p.add(pat);
        p.add(new JLabel("Ap. materno")); p.add(mat);
        p.add(new JLabel("DNI")); p.add(dni);
        p.add(new JLabel("Ciudad")); p.add(ciu);
        p.add(new JLabel("Dirección")); p.add(dir);
        p.add(new JLabel("Teléfono")); p.add(tel);
        p.add(new JLabel("Email")); p.add(ema);
        if (JOptionPane.showConfirmDialog(this, p, "Registrar cliente",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Resultado r = ctrl.registrarCliente(pat.getText(), mat.getText(),
                        nom.getText(), dni.getText(), ciu.getText(),
                        dir.getText(), tel.getText(), ema.getText());
                JOptionPane.showMessageDialog(this, r.getMensaje());
                cargarComboClientes();
            } catch (Exception e) { error(e.getMessage()); }
        }
    }

    private void dialogoRegistrarCuenta() {
        JTextField cli = new JTextField(clienteActual() == null
                ? "" : clienteActual());
        JComboBox<String> mon = new JComboBox<>(new String[]{"Dólares", "Soles"});
        JPanel p = new JPanel(new GridLayout(0, 2, 4, 4));
        p.add(new JLabel("Código cliente")); p.add(cli);
        p.add(new JLabel("Moneda")); p.add(mon);
        if (JOptionPane.showConfirmDialog(this, p,
                "Registrar cuenta (cliente cargado prefijado)",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                result(ctrl.registrarCuenta(cli.getText().trim(),
                        mon.getSelectedIndex() == 1 ? "01" : "02"));
            } catch (Exception e) { error(e.getMessage()); }
        }
    }

    private void dialogoEliminarCuenta() {
        java.util.List<ec.edu.monster.ws.CuentaResumen> ct = ctrl.getCuentas();
        String cta;
        if (ct != null && !ct.isEmpty()) {
            String[] items = new String[ct.size()];
            for (int i = 0; i < ct.size(); i++) {
                ec.edu.monster.ws.CuentaResumen c = ct.get(i);
                items[i] = c.getCodigoCuenta() + "  ·  "
                        + String.format("%,.2f", c.getSaldo()) + " "
                        + Moneda.nombre(c.getMoneda()) + "  ·  " + c.getEstado();
            }
            JComboBox<String> cbo = new JComboBox<>(items);
            int op = JOptionPane.showConfirmDialog(this, cbo,
                    "Eliminar cuenta de " + ct.get(0).getNombreCliente(),
                    JOptionPane.OK_CANCEL_OPTION);
            if (op != JOptionPane.OK_OPTION || cbo.getSelectedIndex() < 0) return;
            cta = ct.get(cbo.getSelectedIndex()).getCodigoCuenta();
        } else {
            cta = JOptionPane.showInputDialog(this,
                    "Código de cuenta a eliminar (se borran sus movimientos):");
            if (cta == null || cta.isBlank()) return;
        }
        if (JOptionPane.showConfirmDialog(this,
                "¿Eliminar la cuenta " + cta + " y sus movimientos?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                result(ctrl.eliminarCuenta(cta.trim()));
            } catch (Exception e) { error(e.getMessage()); }
        }
    }
}
