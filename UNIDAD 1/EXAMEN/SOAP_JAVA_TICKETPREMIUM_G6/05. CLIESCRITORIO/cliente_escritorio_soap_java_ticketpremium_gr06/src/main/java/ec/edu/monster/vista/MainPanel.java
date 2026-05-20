package ec.edu.monster.vista;

import ec.edu.monster.controlador.TicketController;
import ec.edu.monster.modelo.ComprobanteCompra;
import ec.edu.monster.servicio.GeneradorComprobantePDF;
import ec.edu.monster.util.Moneda;
import ec.edu.monster.ws.Factura;
import ec.edu.monster.ws.Localidad;
import ec.edu.monster.ws.Partido;
import ec.edu.monster.ws.Resultado;
import ec.edu.monster.ws.ResumenLocalidad;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 * Vista principal — JTabbedPane con 3 pestanas:
 *   1) Comprar boletos    (cualquier rol)
 *   2) Mis facturas       (historial del usuario)
 *   3) Reporte de ventas  (solo ADMIN)
 */
public class MainPanel extends JPanel {

    private static final BigDecimal TASA_IVA = new BigDecimal("0.15");
    private static final DateTimeFormatter FECHA_FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final TicketController ctrl;

    /** Comprobantes generados en esta sesion (clave = idFactura). */
    private final Map<Integer, ComprobanteCompra> comprobantes = new HashMap<>();

    // --- Tab Comprar ---
    private final JComboBox<PartidoItem> cboPartido = new JComboBox<>();
    private final DefaultTableModel mLoc = new DefaultTableModel(
            new Object[]{"Localidad", "Disponibilidad", "Precio"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tblLoc = new JTable(mLoc);
    private final JSpinner spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private final JLabel lblSubtotal = new JLabel("$ 0.00");
    private final JLabel lblIva      = new JLabel("$ 0.00");
    private final JLabel lblTotal    = new JLabel("$ 0.00");

    // --- Tab Facturas ---
    // Columnas se reconfiguran segun rol en construirTabFacturas()
    private final DefaultTableModel mFact = new DefaultTableModel(
            new Object[]{"# Factura", "Fecha", "Subtotal", "IVA", "Total"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    /** Indice de la columna ID factura (varia segun haya o no columna Cliente). */
    private int colIdFactura = 0;

    // --- Tab Reporte ---
    private final JComboBox<PartidoItem> cboPartidoReporte = new JComboBox<>();
    private final DefaultTableModel mRep = new DefaultTableModel(
            new Object[]{"Localidad", "Vendidos", "Total recaudado"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tblReporte = new JTable(mRep);
    private final JButton btnPdf = new JButton("Descargar PDF");
    private String reporteHeaderPartido = "";

    public MainPanel(TicketController ctrl, Runnable onLogout) {
        this.ctrl = ctrl;
        setLayout(new BorderLayout(8, 8));

        boolean admin = ctrl.getSesion().isAdmin();

        // ---------- Encabezado ----------
        JPanel north = new JPanel(new BorderLayout());
        north.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JPanel oeste = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        oeste.add(Img.label("/images/moster.png", 40));
        JLabel lblInfo = new JLabel("Hola, " + ctrl.getSesion().getNombre()
                + "   [" + ctrl.getSesion().getUsuario().getRol() + "]");
        lblInfo.setFont(lblInfo.getFont().deriveFont(Font.BOLD));
        oeste.add(lblInfo);
        north.add(oeste, BorderLayout.WEST);

        JPanel este = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        JButton btnRefresh = new JButton("Actualizar partidos");
        JButton btnSalir = new JButton("Cerrar sesion");
        este.add(btnRefresh);
        este.add(btnSalir);
        north.add(este, BorderLayout.EAST);
        add(north, BorderLayout.NORTH);

        // ---------- Tabs ----------
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Comprar boletos", construirTabComprar());
        tabs.addTab("Mis facturas",    construirTabFacturas());
        if (admin) {
            tabs.addTab("Reporte de ventas", construirTabReporte());
            tabs.addTab("Administracion",    construirTabAdmin());
        }
        add(tabs, BorderLayout.CENTER);

        // ---------- Listeners ----------
        btnRefresh.addActionListener(e -> cargarPartidos());
        btnSalir.addActionListener(e -> onLogout.run());

        // Carga inicial
        cargarPartidos();
        cargarFacturas();
    }

    // ============================================================================
    // TAB 1 - COMPRAR
    // ============================================================================
    private JPanel construirTabComprar() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Seleccion del partido (combo se ajusta al ancho disponible)
        JPanel arriba = new JPanel(new BorderLayout(8, 4));
        arriba.add(new JLabel("Partido:"), BorderLayout.WEST);
        arriba.add(cboPartido, BorderLayout.CENTER);
        p.add(arriba, BorderLayout.NORTH);

        // Tabla de localidades
        tblLoc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLoc.setRowHeight(24);
        p.add(new JScrollPane(tblLoc), BorderLayout.CENTER);

        // Resumen + accion (2 filas para que sea responsive)
        JPanel sur = new JPanel(new GridLayout(0, 1, 4, 4));
        sur.setBorder(BorderFactory.createTitledBorder("Resumen de la compra"));

        // Fila 1: cantidad (izq) + boton comprar (der)
        JPanel fila1 = new JPanel(new BorderLayout(8, 4));
        JPanel cantPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        cantPanel.add(new JLabel("Cantidad:"));
        spCantidad.setPreferredSize(new Dimension(80, 26));
        cantPanel.add(spCantidad);
        fila1.add(cantPanel, BorderLayout.WEST);

        JButton btnComprar = new JButton("Comprar boletos");
        btnComprar.setFont(btnComprar.getFont().deriveFont(Font.BOLD));
        JPanel botonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botonPanel.add(btnComprar);
        fila1.add(botonPanel, BorderLayout.EAST);
        sur.add(fila1);

        // Fila 2: subtotal / iva / total en grid uniforme
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 14f));
        lblTotal.setForeground(new Color(20, 100, 20));
        JPanel fila2 = new JPanel(new GridLayout(1, 3, 8, 0));
        fila2.add(panelMonto("Subtotal:", lblSubtotal));
        fila2.add(panelMonto("IVA (15%):", lblIva));
        fila2.add(panelMonto("Total:", lblTotal));
        sur.add(fila2);

        p.add(sur, BorderLayout.SOUTH);

        // Listeners
        cboPartido.addActionListener(e -> cargarLocalidades());
        tblLoc.getSelectionModel().addListSelectionListener(e -> recalcular());
        spCantidad.addChangeListener(e -> recalcular());
        btnComprar.addActionListener(e -> accionComprar());

        return p;
    }

    private void cargarPartidos() {
        try {
            List<Partido> partidos = ctrl.partidosDisponibles();
            DefaultComboBoxModel<PartidoItem> m1 = new DefaultComboBoxModel<>();
            DefaultComboBoxModel<PartidoItem> m2 = new DefaultComboBoxModel<>();
            for (Partido p : partidos) {
                m1.addElement(new PartidoItem(p));
                m2.addElement(new PartidoItem(p));
            }
            cboPartido.setModel(m1);
            cboPartidoReporte.setModel(m2);
            cargarLocalidades();
        } catch (Exception ex) {
            error("No se pudieron cargar los partidos:\n" + ex.getMessage());
        }
    }

    private void cargarLocalidades() {
        mLoc.setRowCount(0);
        PartidoItem sel = (PartidoItem) cboPartido.getSelectedItem();
        if (sel == null) return;
        try {
            for (Localidad l : ctrl.localidadesDe(sel.partido.getCodigo())) {
                mLoc.addRow(new Object[]{
                        l.getCodigoLocalidad(),
                        l.getDisponibilidad(),
                        Moneda.fmt(l.getPrecio())
                });
            }
            recalcular();
        } catch (Exception ex) {
            error("No se pudieron cargar las localidades:\n" + ex.getMessage());
        }
    }

    private void recalcular() {
        BigDecimal precio = precioSeleccionado();
        int cantidad = (int) spCantidad.getValue();
        if (precio == null) {
            lblSubtotal.setText("$ 0.00");
            lblIva.setText("$ 0.00");
            lblTotal.setText("$ 0.00");
            return;
        }
        BigDecimal sub = precio.multiply(BigDecimal.valueOf(cantidad));
        BigDecimal iva = sub.multiply(TASA_IVA).setScale(2, RoundingMode.HALF_UP);
        BigDecimal tot = sub.add(iva);
        lblSubtotal.setText(Moneda.fmt(sub));
        lblIva.setText(Moneda.fmt(iva));
        lblTotal.setText(Moneda.fmt(tot));
    }

    private BigDecimal precioSeleccionado() {
        int row = tblLoc.getSelectedRow();
        if (row < 0) return null;
        // Columna 2 es "$ 1,234.56"
        String s = String.valueOf(mLoc.getValueAt(row, 2)).replace("$", "").replace(",", "").trim();
        try { return new BigDecimal(s); } catch (Exception e) { return null; }
    }

    private String localidadSeleccionada() {
        int row = tblLoc.getSelectedRow();
        return row < 0 ? null : String.valueOf(mLoc.getValueAt(row, 0));
    }

    private void accionComprar() {
        PartidoItem partido = (PartidoItem) cboPartido.getSelectedItem();
        String localidad = localidadSeleccionada();
        if (partido == null || localidad == null) {
            error("Selecciona un partido y una localidad de la tabla.");
            return;
        }
        int cantidad = (int) spCantidad.getValue();
        int conf = JOptionPane.showConfirmDialog(this,
                String.format(
                    "Confirmar compra:\n\nPartido:   %s\nLocalidad: %s\nCantidad:  %d\nTotal:     %s",
                    partido.toString(), localidad, cantidad, lblTotal.getText()),
                "Confirmar compra", JOptionPane.OK_CANCEL_OPTION);
        if (conf != JOptionPane.OK_OPTION) return;

        try {
            Resultado r = ctrl.comprar(partido.partido.getCodigo(), localidad, cantidad);
            if (r.isExito()) {
                Factura f = r.getFactura();
                ComprobanteCompra comp = construirComprobante(partido, localidad, cantidad, f);
                comprobantes.put(f.getIdFactura(), comp);

                String mensaje = String.format(
                        "%s\n\nComprobante: %s\nFactura #%d\nSubtotal: %s\nIVA: %s\nTotal: %s",
                        r.getMensaje(), comp.getCodigoR(), f.getIdFactura(),
                        Moneda.fmt(f.getSubtotal()),
                        Moneda.fmt(f.getIva()),
                        Moneda.fmt(f.getTotal()));
                int op = JOptionPane.showOptionDialog(this, mensaje,
                        "Compra exitosa", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null,
                        new Object[]{"Descargar comprobante PDF", "Cerrar"}, "Descargar comprobante PDF");
                if (op == 0) descargarComprobantePDF(comp);

                cargarLocalidades();   // refresca stock
                cargarFacturas();      // refresca historial
            } else {
                error(r.getMensaje());
            }
        } catch (Exception ex) {
            error("Error registrando la compra:\n" + ex.getMessage());
        }
    }

    private ComprobanteCompra construirComprobante(PartidoItem partido, String localidad,
                                                   int cantidad, Factura f) {
        ComprobanteCompra c = new ComprobanteCompra();
        c.setIdFactura(f.getIdFactura());
        c.setCodigoR(generarCodigoR());
        c.setFecha(LocalDateTime.now().format(FECHA_FORMATO));
        c.setUsuario(ctrl.getSesion().getNombre());
        c.setPartido(partido.partido.getEquipoLocal() + " vs " + partido.partido.getEquipoVisita());
        c.setLocalidad(localidad);
        c.setCantidad(cantidad);
        c.setSubtotal(f.getSubtotal());
        c.setIva(f.getIva());
        c.setTotal(f.getTotal());
        return c;
    }

    private static String generarCodigoR() {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        int rand = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "R-" + fecha + "-" + rand;
    }

    private void descargarComprobantePDF(ComprobanteCompra comp) {
        String sugerido = (comp.getCodigoR() + "-" + comp.getIdFactura())
                .replaceAll("[^A-Za-z0-9_-]", "_") + ".pdf";
        File dest = elegirArchivoPDF(sugerido);
        if (dest == null) return;
        try {
            byte[] pdf = GeneradorComprobantePDF.comprobante(comp);
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                fos.write(pdf);
            }
            abrirArchivo(dest);
        } catch (Exception ex) {
            error("No se pudo generar el comprobante:\n" + ex.getMessage());
        }
    }

    private File elegirArchivoPDF(String nombreSugerido) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar como PDF");
        fc.setSelectedFile(new File(nombreSugerido));
        fc.setFileFilter(new FileNameExtensionFilter("Documento PDF (*.pdf)", "pdf"));
        int op = fc.showSaveDialog(this);
        if (op != JFileChooser.APPROVE_OPTION) return null;
        File f = fc.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(".pdf")) {
            f = new File(f.getParentFile(), f.getName() + ".pdf");
        }
        return f;
    }

    private void abrirArchivo(File f) {
        try {
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(f);
        } catch (Exception ignore) { }
    }

    // ============================================================================
    // TAB 2 - FACTURAS
    // ============================================================================
    private JPanel construirTabFacturas() {
        // Reconfigurar columnas segun rol: admin ve columna Cliente
        boolean admin = ctrl.getSesion().isAdmin();
        if (admin) {
            mFact.setColumnIdentifiers(new Object[]{"# Factura", "Cliente", "Fecha", "Subtotal", "IVA", "Total"});
        } else {
            mFact.setColumnIdentifiers(new Object[]{"# Factura", "Fecha", "Subtotal", "IVA", "Total"});
        }
        colIdFactura = 0;

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel(admin ? "Todas las facturas del sistema" : "Mis facturas");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 14f));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        p.add(titulo, BorderLayout.NORTH);

        JTable tbl = new JTable(mFact);
        tbl.setRowHeight(24);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        p.add(new JScrollPane(tbl), BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnPdfFactura = new JButton("Descargar PDF");
        JButton btnRefrescar  = new JButton("Actualizar");
        sur.add(btnPdfFactura);
        sur.add(btnRefrescar);
        p.add(sur, BorderLayout.SOUTH);

        btnRefrescar.addActionListener(e -> cargarFacturas());
        btnPdfFactura.addActionListener(e -> {
            int row = tbl.getSelectedRow();
            if (row < 0) { error("Selecciona una factura de la tabla."); return; }
            int idFactura = ((Number) mFact.getValueAt(row, colIdFactura)).intValue();
            ComprobanteCompra comp = comprobantes.get(idFactura);
            if (comp == null) {
                error("Solo puedes descargar el PDF de facturas generadas en esta sesion.\n"
                        + "Realiza una compra nueva para obtener el comprobante completo.");
                return;
            }
            descargarComprobantePDF(comp);
        });
        return p;
    }

    private void cargarFacturas() {
        mFact.setRowCount(0);
        boolean admin = ctrl.getSesion().isAdmin();
        try {
            for (Factura f : ctrl.misFacturas()) {
                if (admin) {
                    String nombre = f.getUsuarioNombre();
                    if (nombre == null || nombre.isEmpty()) nombre = "id=" + f.getIdUsuario();
                    mFact.addRow(new Object[]{
                            f.getIdFactura(),
                            nombre,
                            f.getFecha(),
                            Moneda.fmt(f.getSubtotal()),
                            Moneda.fmt(f.getIva()),
                            Moneda.fmt(f.getTotal())
                    });
                } else {
                    mFact.addRow(new Object[]{
                            f.getIdFactura(),
                            f.getFecha(),
                            Moneda.fmt(f.getSubtotal()),
                            Moneda.fmt(f.getIva()),
                            Moneda.fmt(f.getTotal())
                    });
                }
            }
        } catch (Exception ex) {
            error("No se pudieron cargar las facturas:\n" + ex.getMessage());
        }
    }

    // ============================================================================
    // TAB 3 - REPORTE  (solo ADMIN)
    // ============================================================================
    private JPanel construirTabReporte() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel arriba = new JPanel(new BorderLayout(8, 4));
        arriba.add(new JLabel("Partido:"), BorderLayout.WEST);
        arriba.add(cboPartidoReporte, BorderLayout.CENTER);
        JButton btnGenerar = new JButton("Generar reporte");
        JPanel botonesRep = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botonesRep.add(btnGenerar);
        botonesRep.add(btnPdf);
        arriba.add(botonesRep, BorderLayout.EAST);
        p.add(arriba, BorderLayout.NORTH);

        tblReporte.setRowHeight(24);
        p.add(new JScrollPane(tblReporte), BorderLayout.CENTER);

        btnPdf.setEnabled(false);
        btnPdf.setToolTipText("Genera primero un reporte para habilitar la descarga");

        btnGenerar.addActionListener(e -> {
            PartidoItem sel = (PartidoItem) cboPartidoReporte.getSelectedItem();
            if (sel == null) return;
            mRep.setRowCount(0);
            try {
                int totalVendidos = 0;
                BigDecimal totalRec = BigDecimal.ZERO;
                List<ResumenLocalidad> filas = ctrl.resumenVentas(sel.partido.getCodigo());
                for (ResumenLocalidad r : filas) {
                    mRep.addRow(new Object[]{
                            r.getLocalidad(), r.getVendidos(),
                            Moneda.fmt(r.getTotalRecaudado())
                    });
                    totalVendidos += r.getVendidos();
                    totalRec = totalRec.add(r.getTotalRecaudado());
                }
                if (!filas.isEmpty()) {
                    mRep.addRow(new Object[]{"-- TOTAL --", totalVendidos, Moneda.fmt(totalRec)});
                    reporteHeaderPartido = sel.toString();
                    ultimoReporte = filas;
                    btnPdf.setEnabled(true);
                } else {
                    mRep.addRow(new Object[]{"(sin ventas registradas)", "", ""});
                    ultimoReporte = java.util.Collections.emptyList();
                    btnPdf.setEnabled(false);
                }
            } catch (Exception ex) {
                error("No se pudo generar el reporte:\n" + ex.getMessage());
            }
        });

        btnPdf.addActionListener(e -> descargarReportePdf());

        return p;
    }

    private List<ResumenLocalidad> ultimoReporte = java.util.Collections.emptyList();

    /** Genera el reporte de ventas directamente como PDF (PDFBox), sin pasar por la impresora. */
    private void descargarReportePdf() {
        if (ultimoReporte.isEmpty()) {
            error("No hay datos para exportar. Genera un reporte primero.");
            return;
        }
        String nombre = ("reporte-" + reporteHeaderPartido).replaceAll("[^A-Za-z0-9_-]", "_") + ".pdf";
        File dest = elegirArchivoPDF(nombre);
        if (dest == null) return;
        try {
            byte[] pdf = GeneradorComprobantePDF.reporteVentas(
                    reporteHeaderPartido, ultimoReporte, ctrl.getSesion().getNombre());
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                fos.write(pdf);
            }
            abrirArchivo(dest);
        } catch (Exception ex) {
            error("No se pudo generar el reporte:\n" + ex.getMessage());
        }
    }

    // ============================================================================
    // TAB 4 - ADMINISTRACION  (solo ADMIN)
    // ============================================================================
    private final DefaultTableModel mAdminPartidos = new DefaultTableModel(
            new Object[]{"Codigo", "Local", "Visita", "Fecha", "Lugar"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tblAdminPartidos = new JTable(mAdminPartidos);

    private final JComboBox<PartidoItem> cboAdminPartido = new JComboBox<>();
    private final DefaultTableModel mAdminLocs = new DefaultTableModel(
            new Object[]{"ID", "Localidad", "Disponibilidad", "Precio"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tblAdminLocs = new JTable(mAdminLocs);

    private JPanel construirTabAdmin() {
        JTabbedPane sub = new JTabbedPane();
        sub.addTab("Partidos",     construirSubAdminPartidos());
        sub.addTab("Localidades",  construirSubAdminLocalidades());

        JPanel cont = new JPanel(new BorderLayout());
        cont.add(sub, BorderLayout.CENTER);

        // Cargar datos al cambiar de tab tambien actualiza lo que el resto ve
        cargarAdminPartidos();
        return cont;
    }

    // ----------------------------------------------------------------------------
    // Sub-tab: Partidos
    // ----------------------------------------------------------------------------
    private JPanel construirSubAdminPartidos() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        tblAdminPartidos.setRowHeight(24);
        tblAdminPartidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        p.add(new JScrollPane(tblAdminPartidos), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        JButton btnNuevo    = new JButton("Nuevo partido");
        JButton btnEditar   = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefresh  = new JButton("Actualizar");
        botones.add(btnNuevo);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnRefresh);
        p.add(botones, BorderLayout.SOUTH);

        btnNuevo.addActionListener(e -> dialogoPartido(null));
        btnEditar.addActionListener(e -> {
            Object[] fila = filaPartidoSeleccionada();
            if (fila != null) dialogoPartido(fila);
        });
        btnEliminar.addActionListener(e -> accionEliminarPartido());
        btnRefresh.addActionListener(e -> { cargarPartidos(); cargarAdminPartidos(); });

        return p;
    }

    private void cargarAdminPartidos() {
        mAdminPartidos.setRowCount(0);
        try {
            for (Partido pa : ctrl.partidosDisponibles()) {
                mAdminPartidos.addRow(new Object[]{
                        pa.getCodigo(),
                        pa.getEquipoLocal(),
                        pa.getEquipoVisita(),
                        pa.getFecha(),
                        pa.getLugar()
                });
            }
        } catch (Exception ex) {
            error("No se pudo cargar partidos:\n" + ex.getMessage());
        }
    }

    private Object[] filaPartidoSeleccionada() {
        int row = tblAdminPartidos.getSelectedRow();
        if (row < 0) {
            error("Selecciona un partido de la tabla.");
            return null;
        }
        return new Object[]{
                mAdminPartidos.getValueAt(row, 0),
                mAdminPartidos.getValueAt(row, 1),
                mAdminPartidos.getValueAt(row, 2),
                mAdminPartidos.getValueAt(row, 3),
                mAdminPartidos.getValueAt(row, 4)
        };
    }

    /** Si fila == null -> modo nuevo. Si != null -> modo edicion. */
    private void dialogoPartido(Object[] fila) {
        boolean editar = fila != null;
        JTextField txtLocal  = new JTextField(20);
        JTextField txtVisita = new JTextField(20);
        JTextField txtFecha  = new JTextField("2026-12-31 19:00:00", 20);
        JTextField txtLugar  = new JTextField(20);

        if (editar) {
            txtLocal.setText(String.valueOf(fila[1]));
            txtVisita.setText(String.valueOf(fila[2]));
            String f = String.valueOf(fila[3]);
            // Recortar ".0" final si viene de Timestamp.toString
            if (f.endsWith(".0")) f = f.substring(0, f.length() - 2);
            txtFecha.setText(f);
            txtLugar.setText(String.valueOf(fila[4]));
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setPreferredSize(new Dimension(440, 140));
        form.add(new JLabel("Equipo local:"));        form.add(txtLocal);
        form.add(new JLabel("Equipo visita:"));       form.add(txtVisita);
        form.add(new JLabel("Fecha (yyyy-MM-dd HH:mm:ss):")); form.add(txtFecha);
        form.add(new JLabel("Lugar:"));               form.add(txtLugar);

        int op = JOptionPane.showConfirmDialog(this, form,
                editar ? "Editar partido #" + fila[0] : "Nuevo partido",
                JOptionPane.OK_CANCEL_OPTION);
        if (op != JOptionPane.OK_OPTION) return;

        try {
            Resultado r = editar
                    ? ctrl.actualizarPartido((int) fila[0], txtLocal.getText(),
                            txtVisita.getText(), txtFecha.getText(), txtLugar.getText())
                    : ctrl.registrarPartido(txtLocal.getText(), txtVisita.getText(),
                            txtFecha.getText(), txtLugar.getText());
            JOptionPane.showMessageDialog(this, r.getMensaje(),
                    r.isExito() ? "OK" : "Error",
                    r.isExito() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            if (r.isExito()) {
                cargarPartidos();
                cargarAdminPartidos();
            }
        } catch (Exception ex) {
            error("Error en la operacion:\n" + ex.getMessage());
        }
    }

    private void accionEliminarPartido() {
        Object[] fila = filaPartidoSeleccionada();
        if (fila == null) return;
        int codigo = (int) fila[0];
        if (JOptionPane.showConfirmDialog(this,
                "Eliminar partido #" + codigo + " (" + fila[1] + " vs " + fila[2] + ")?",
                "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            Resultado r = ctrl.eliminarPartido(codigo);
            JOptionPane.showMessageDialog(this, r.getMensaje(),
                    r.isExito() ? "OK" : "Error",
                    r.isExito() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            if (r.isExito()) { cargarPartidos(); cargarAdminPartidos(); }
        } catch (Exception ex) {
            error("Error eliminando:\n" + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------------------
    // Sub-tab: Localidades (de un partido)
    // ----------------------------------------------------------------------------
    private JPanel construirSubAdminLocalidades() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel arriba = new JPanel(new BorderLayout(8, 4));
        arriba.add(new JLabel("Partido:"), BorderLayout.WEST);
        cboAdminPartido.setModel(cboPartido.getModel());
        arriba.add(cboAdminPartido, BorderLayout.CENTER);
        p.add(arriba, BorderLayout.NORTH);

        tblAdminLocs.setRowHeight(24);
        tblAdminLocs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        p.add(new JScrollPane(tblAdminLocs), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        JButton btnNuevo    = new JButton("Nueva localidad");
        JButton btnEditar   = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefresh  = new JButton("Actualizar");
        botones.add(btnNuevo);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnRefresh);
        p.add(botones, BorderLayout.SOUTH);

        cboAdminPartido.addActionListener(e -> cargarAdminLocalidades());
        btnNuevo.addActionListener(e -> dialogoLocalidad(null));
        btnEditar.addActionListener(e -> {
            int row = tblAdminLocs.getSelectedRow();
            if (row < 0) { error("Selecciona una localidad."); return; }
            dialogoLocalidad(new Object[]{
                    mAdminLocs.getValueAt(row, 0),
                    mAdminLocs.getValueAt(row, 1),
                    mAdminLocs.getValueAt(row, 2),
                    mAdminLocs.getValueAt(row, 3)
            });
        });
        btnEliminar.addActionListener(e -> accionEliminarLocalidad());
        btnRefresh.addActionListener(e -> cargarAdminLocalidades());

        cargarAdminLocalidades();
        return p;
    }

    private void cargarAdminLocalidades() {
        mAdminLocs.setRowCount(0);
        PartidoItem sel = (PartidoItem) cboAdminPartido.getSelectedItem();
        if (sel == null) return;
        try {
            for (Localidad l : ctrl.localidadesAdmin(sel.partido.getCodigo())) {
                mAdminLocs.addRow(new Object[]{
                        l.getId(),
                        l.getCodigoLocalidad(),
                        l.getDisponibilidad(),
                        Moneda.fmt(l.getPrecio())
                });
            }
        } catch (Exception ex) {
            error("No se pudo cargar localidades:\n" + ex.getMessage());
        }
    }

    private void dialogoLocalidad(Object[] fila) {
        PartidoItem sel = (PartidoItem) cboAdminPartido.getSelectedItem();
        if (sel == null) { error("Selecciona un partido primero."); return; }
        boolean editar = fila != null;

        JTextField txtCodigo = new JTextField(10);
        JTextField txtDispo  = new JTextField("100", 10);
        JTextField txtPrecio = new JTextField("10.00", 10);

        if (editar) {
            txtCodigo.setText(String.valueOf(fila[1]));
            txtCodigo.setEditable(false);   // No se cambia el codigo en edicion
            txtDispo.setText(String.valueOf(fila[2]));
            String precio = String.valueOf(fila[3]).replace("$", "").replace(",", "").trim();
            txtPrecio.setText(precio);
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setPreferredSize(new Dimension(400, 110));
        form.add(new JLabel("Codigo (GENERAL, TRIBUNA, ...):")); form.add(txtCodigo);
        form.add(new JLabel("Disponibilidad:"));                 form.add(txtDispo);
        form.add(new JLabel("Precio:"));                          form.add(txtPrecio);

        int op = JOptionPane.showConfirmDialog(this, form,
                editar ? "Editar localidad #" + fila[0]
                       : "Nueva localidad para partido " + sel.partido.getCodigo(),
                JOptionPane.OK_CANCEL_OPTION);
        if (op != JOptionPane.OK_OPTION) return;

        try {
            int disp = Integer.parseInt(txtDispo.getText().trim());
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            Resultado r = editar
                    ? ctrl.actualizarLocalidad((int) fila[0], disp, precio)
                    : ctrl.registrarLocalidad(sel.partido.getCodigo(),
                            txtCodigo.getText(), disp, precio);
            JOptionPane.showMessageDialog(this, r.getMensaje(),
                    r.isExito() ? "OK" : "Error",
                    r.isExito() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            if (r.isExito()) { cargarAdminLocalidades(); cargarLocalidades(); }
        } catch (NumberFormatException nfe) {
            error("Disponibilidad o precio invalidos.");
        } catch (Exception ex) {
            error("Error en la operacion:\n" + ex.getMessage());
        }
    }

    private void accionEliminarLocalidad() {
        int row = tblAdminLocs.getSelectedRow();
        if (row < 0) { error("Selecciona una localidad."); return; }
        int id = (int) mAdminLocs.getValueAt(row, 0);
        String cod = String.valueOf(mAdminLocs.getValueAt(row, 1));
        if (JOptionPane.showConfirmDialog(this,
                "Eliminar localidad " + cod + " (id=" + id + ")?",
                "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            Resultado r = ctrl.eliminarLocalidad(id);
            JOptionPane.showMessageDialog(this, r.getMensaje(),
                    r.isExito() ? "OK" : "Error",
                    r.isExito() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            if (r.isExito()) cargarAdminLocalidades();
        } catch (Exception ex) {
            error("Error eliminando:\n" + ex.getMessage());
        }
    }

    // ============================================================================
    // Util
    // ============================================================================
    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Mini-panel "Etiqueta: valor" usado en la fila de totales. */
    private static JPanel panelMonto(String etiqueta, JLabel valor) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.add(new JLabel(etiqueta));
        p.add(valor);
        return p;
    }

    /** Item del combo de partidos. toString() es lo que se renderiza. */
    private static class PartidoItem {
        final Partido partido;
        PartidoItem(Partido p) { this.partido = p; }
        @Override public String toString() {
            return String.format("[%d] %s vs %s  -  %s",
                    partido.getCodigo(), partido.getEquipoLocal(),
                    partido.getEquipoVisita(), partido.getFecha());
        }
    }
}
