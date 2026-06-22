package ec.edu.monster.vista;

import ec.edu.monster.controlador.TicketController;
import ec.edu.monster.servicio.GeneradorComprobantePDF;
import ec.edu.monster.util.Moneda;
import ec.edu.monster.ws.Estadio;
import ec.edu.monster.ws.Factura;
import ec.edu.monster.ws.Localidad;
import ec.edu.monster.ws.Partido;
import ec.edu.monster.ws.Resultado;
import ec.edu.monster.ws.ResumenLocalidad;
import ec.edu.monster.ws.Seleccion;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Pestana "Administracion" (solo ADMIN): CRUD de partidos (combos de
 * selecciones y estadios) y localidades, reporte resumen de ventas por
 * partido y listado de todas las facturas.
 */
public class AdminPanel extends JPanel {

    private final TicketController ctrl;

    /* ---- subpestana Partidos ---- */
    private final DefaultTableModel mPartidos = new DefaultTableModel(
            new Object[]{"Codigo", "Grupo", "Local", "Visita", "Fecha",
                         "Estadio", "Ciudad"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tPartidos = new JTable(mPartidos);
    private final List<Partido> partidos = new ArrayList<>();
    private final JComboBox<Seleccion> cboLocal = new JComboBox<>();
    private final JComboBox<Seleccion> cboVisita = new JComboBox<>();
    private final JComboBox<Estadio> cboEstadio = new JComboBox<>();
    private final JTextField txtFecha = new JTextField(14);
    private final JTextField txtGrupo = new JTextField(4);

    /* ---- subpestana Localidades ---- */
    private final JComboBox<Partido> cboPartidoLoc = new JComboBox<>();
    private final DefaultTableModel mLocalidades = new DefaultTableModel(
            new Object[]{"Id", "Categoria", "Precio", "Disponibilidad"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tLocalidades = new JTable(mLocalidades);
    private final List<Localidad> localidades = new ArrayList<>();
    private final JComboBox<String> cboCategoria =
            new JComboBox<>(new String[]{"CAT1", "CAT2", "CAT3", "CAT4"});
    private final JTextField txtPrecio = new JTextField(8);
    private final JTextField txtDisponibilidad = new JTextField(6);

    /* ---- subpestana Reporte ---- */
    private final JComboBox<Partido> cboPartidoRep = new JComboBox<>();
    private final DefaultTableModel mReporte = new DefaultTableModel(
            new Object[]{"Localidad", "Vendidos", "Total recaudado"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final List<ResumenLocalidad> reporteActual = new ArrayList<>();

    /* ---- subpestana Facturas ---- */
    private final DefaultTableModel mFacturas = new DefaultTableModel(
            new Object[]{"Factura", "Cliente", "Fecha", "Tipo pago", "Total"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tFacturas = new JTable(mFacturas);
    private final List<Factura> facturas = new ArrayList<>();

    public AdminPanel(TicketController ctrl) {
        this.ctrl = ctrl;
        setLayout(new BorderLayout(0, 10));
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new javax.swing.BoxLayout(titulos, javax.swing.BoxLayout.Y_AXIS));
        titulos.add(Estilo.titulo("Administracion"));
        titulos.add(Estilo.subtitulo("Gestion de partidos, localidades, reporte de ventas y facturas (solo ADMIN)."));
        add(titulos, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Estilo.fuente(Font.BOLD, 12));
        tabs.setBackground(Estilo.FONDO);
        tabs.addTab("Partidos", construirTabPartidos());
        tabs.addTab("Localidades", construirTabLocalidades());
        tabs.addTab("Reporte de ventas", construirTabReporte());
        tabs.addTab("Todas las facturas", construirTabFacturas());
        add(tabs, BorderLayout.CENTER);

        cargarCatalogos();
        cargarPartidos();
        cargarFacturas();
    }

    /* ================================================== TAB PARTIDOS */

    private JPanel construirTabPartidos() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Estilo.FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        Estilo.tabla(tPartidos);
        tPartidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarFormPartido();
        });
        p.add(Estilo.scroll(tPartidos), BorderLayout.CENTER);

        cboLocal.setRenderer(renderSeleccion());
        cboVisita.setRenderer(renderSeleccion());
        cboEstadio.setRenderer(new DefaultListCellRenderer() {
            @Override public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean sel, boolean foc) {
                super.getListCellRendererComponent(list, value, index, sel, foc);
                if (value instanceof Estadio e) {
                    setText(e.getNombreFifa() + " (" + e.getCiudad() + ")");
                }
                return this;
            }
        });

        JPanel form = Estilo.tarjeta();
        form.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 4));
        form.add(etiqueta("Local:"));
        form.add(cboLocal);
        form.add(etiqueta("Visita:"));
        form.add(cboVisita);
        form.add(etiqueta("Estadio:"));
        form.add(cboEstadio);
        form.add(etiqueta("Fecha (yyyy-MM-dd HH:mm):"));
        form.add(txtFecha);
        form.add(etiqueta("Grupo:"));
        form.add(txtGrupo);

        JButton btnNuevo = Estilo.botonExito("Registrar");
        btnNuevo.addActionListener(e -> registrarPartido());
        JButton btnActualizar = Estilo.botonPrimario("Actualizar");
        btnActualizar.addActionListener(e -> actualizarPartido());
        JButton btnEliminar = Estilo.botonPeligro("Eliminar");
        btnEliminar.addActionListener(e -> eliminarPartido());
        JButton btnRefrescar = Estilo.botonPlano("Refrescar");
        btnRefrescar.addActionListener(e -> cargarPartidos());
        form.add(btnNuevo);
        form.add(btnActualizar);
        form.add(btnEliminar);
        form.add(btnRefrescar);

        p.add(form, BorderLayout.SOUTH);
        return p;
    }

    private DefaultListCellRenderer renderSeleccion() {
        return new DefaultListCellRenderer() {
            @Override public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean sel, boolean foc) {
                super.getListCellRendererComponent(list, value, index, sel, foc);
                if (value instanceof Seleccion s) {
                    setText(s.getNombre() + " (Grupo " + s.getGrupo() + ")");
                }
                return this;
            }
        };
    }

    private void cargarCatalogos() {
        try {
            cboLocal.removeAllItems();
            cboVisita.removeAllItems();
            for (Seleccion s : ctrl.listarSelecciones()) {
                cboLocal.addItem(s);
                cboVisita.addItem(s);
            }
            cboEstadio.removeAllItems();
            for (Estadio e : ctrl.listarEstadios()) cboEstadio.addItem(e);
        } catch (Exception ex) {
            error("No se pudieron cargar selecciones/estadios: " + ex.getMessage());
        }
    }

    private void cargarPartidos() {
        try {
            partidos.clear();
            partidos.addAll(ctrl.todosPartidos());
            mPartidos.setRowCount(0);
            for (Partido p : partidos) {
                mPartidos.addRow(new Object[]{p.getCodigo(), nn(p.getGrupo()),
                        nn(p.getEquipoLocal()), nn(p.getEquipoVisita()),
                        nn(p.getFecha()), nn(p.getEstadio()), nn(p.getCiudad())});
            }
            // refrescar combos dependientes
            cboPartidoLoc.removeAllItems();
            cboPartidoRep.removeAllItems();
            for (Partido p : partidos) {
                cboPartidoLoc.addItem(p);
                cboPartidoRep.addItem(p);
            }
        } catch (Exception ex) {
            error("No se pudieron cargar los partidos: " + ex.getMessage());
        }
    }

    private Partido partidoSeleccionado() {
        int fila = tPartidos.getSelectedRow();
        if (fila < 0) return null;
        return partidos.get(tPartidos.convertRowIndexToModel(fila));
    }

    private void cargarFormPartido() {
        Partido p = partidoSeleccionado();
        if (p == null) return;
        seleccionarCombo(cboLocal, p.getIdLocal());
        seleccionarCombo(cboVisita, p.getIdVisita());
        for (int i = 0; i < cboEstadio.getItemCount(); i++) {
            if (cboEstadio.getItemAt(i).getIdEstadio() == p.getIdEstadio()) {
                cboEstadio.setSelectedIndex(i);
                break;
            }
        }
        txtFecha.setText(nn(p.getFecha()));
        txtGrupo.setText(nn(p.getGrupo()));
    }

    private void seleccionarCombo(JComboBox<Seleccion> cbo, int idSeleccion) {
        for (int i = 0; i < cbo.getItemCount(); i++) {
            if (cbo.getItemAt(i).getIdSeleccion() == idSeleccion) {
                cbo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void registrarPartido() {
        Seleccion l = (Seleccion) cboLocal.getSelectedItem();
        Seleccion v = (Seleccion) cboVisita.getSelectedItem();
        Estadio e = (Estadio) cboEstadio.getSelectedItem();
        if (l == null || v == null || e == null) return;
        try {
            Resultado r = ctrl.registrarPartido(l.getIdSeleccion(),
                    v.getIdSeleccion(), e.getIdEstadio(),
                    txtFecha.getText().trim(), txtGrupo.getText().trim());
            mensaje(r);
            if (r.isExito()) cargarPartidos();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void actualizarPartido() {
        Partido p = partidoSeleccionado();
        if (p == null) {
            error("Selecciona un partido de la tabla.");
            return;
        }
        Seleccion l = (Seleccion) cboLocal.getSelectedItem();
        Seleccion v = (Seleccion) cboVisita.getSelectedItem();
        Estadio e = (Estadio) cboEstadio.getSelectedItem();
        if (l == null || v == null || e == null) return;
        try {
            Resultado r = ctrl.actualizarPartido(p.getCodigo(),
                    l.getIdSeleccion(), v.getIdSeleccion(), e.getIdEstadio(),
                    txtFecha.getText().trim(), txtGrupo.getText().trim());
            mensaje(r);
            if (r.isExito()) cargarPartidos();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void eliminarPartido() {
        Partido p = partidoSeleccionado();
        if (p == null) {
            error("Selecciona un partido de la tabla.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this,
                "Eliminar el partido " + p.getEquipoLocal() + " vs "
                + p.getEquipoVisita() + "?", "Confirmar",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            Resultado r = ctrl.eliminarPartido(p.getCodigo());
            mensaje(r);
            if (r.isExito()) cargarPartidos();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    /* =============================================== TAB LOCALIDADES */

    private JPanel construirTabLocalidades() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Estilo.FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        cboPartidoLoc.setRenderer(renderPartido());
        cboPartidoLoc.addActionListener(e -> cargarLocalidades());

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtro.setOpaque(false);
        filtro.add(etiqueta("Partido:"));
        filtro.add(cboPartidoLoc);
        p.add(filtro, BorderLayout.NORTH);

        Estilo.tabla(tLocalidades);
        tLocalidades.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarFormLocalidad();
        });
        p.add(Estilo.scroll(tLocalidades), BorderLayout.CENTER);

        JPanel form = Estilo.tarjeta();
        form.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 4));
        form.add(etiqueta("Categoria:"));
        form.add(cboCategoria);
        form.add(etiqueta("Precio ($):"));
        form.add(txtPrecio);
        form.add(etiqueta("Disponibilidad:"));
        form.add(txtDisponibilidad);

        JButton btnNueva = Estilo.botonExito("Registrar");
        btnNueva.addActionListener(e -> registrarLocalidad());
        JButton btnActualizar = Estilo.botonPrimario("Actualizar");
        btnActualizar.addActionListener(e -> actualizarLocalidad());
        JButton btnEliminar = Estilo.botonPeligro("Eliminar");
        btnEliminar.addActionListener(e -> eliminarLocalidad());
        form.add(btnNueva);
        form.add(btnActualizar);
        form.add(btnEliminar);
        p.add(form, BorderLayout.SOUTH);
        return p;
    }

    private DefaultListCellRenderer renderPartido() {
        return new DefaultListCellRenderer() {
            @Override public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean sel, boolean foc) {
                super.getListCellRendererComponent(list, value, index, sel, foc);
                if (value instanceof Partido p) {
                    setText("#" + p.getCodigo() + " " + p.getEquipoLocal()
                            + " vs " + p.getEquipoVisita());
                }
                return this;
            }
        };
    }

    private void cargarLocalidades() {
        Partido p = (Partido) cboPartidoLoc.getSelectedItem();
        if (p == null) return;
        try {
            localidades.clear();
            localidades.addAll(ctrl.localidadesAdmin(p.getCodigo()));
            mLocalidades.setRowCount(0);
            for (Localidad l : localidades) {
                mLocalidades.addRow(new Object[]{l.getId(), nn(l.getCategoria()),
                        Moneda.fmt(l.getPrecio()), l.getDisponibilidad()});
            }
        } catch (Exception ex) {
            error("No se pudieron cargar las localidades: " + ex.getMessage());
        }
    }

    private Localidad localidadSeleccionada() {
        int fila = tLocalidades.getSelectedRow();
        if (fila < 0) return null;
        return localidades.get(tLocalidades.convertRowIndexToModel(fila));
    }

    private void cargarFormLocalidad() {
        Localidad l = localidadSeleccionada();
        if (l == null) return;
        cboCategoria.setSelectedItem(nn(l.getCategoria()));
        txtPrecio.setText(l.getPrecio() == null ? "" : l.getPrecio().toPlainString());
        txtDisponibilidad.setText(String.valueOf(l.getDisponibilidad()));
    }

    private void registrarLocalidad() {
        Partido p = (Partido) cboPartidoLoc.getSelectedItem();
        if (p == null) return;
        try {
            Resultado r = ctrl.registrarLocalidad(p.getCodigo(),
                    String.valueOf(cboCategoria.getSelectedItem()),
                    Integer.parseInt(txtDisponibilidad.getText().trim()),
                    new BigDecimal(txtPrecio.getText().trim()));
            mensaje(r);
            if (r.isExito()) cargarLocalidades();
        } catch (NumberFormatException nfe) {
            error("Precio o disponibilidad invalidos.");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void actualizarLocalidad() {
        Localidad l = localidadSeleccionada();
        if (l == null) {
            error("Selecciona una localidad de la tabla.");
            return;
        }
        try {
            Resultado r = ctrl.actualizarLocalidad(l.getId(),
                    Integer.parseInt(txtDisponibilidad.getText().trim()),
                    new BigDecimal(txtPrecio.getText().trim()));
            mensaje(r);
            if (r.isExito()) cargarLocalidades();
        } catch (NumberFormatException nfe) {
            error("Precio o disponibilidad invalidos.");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void eliminarLocalidad() {
        Localidad l = localidadSeleccionada();
        if (l == null) {
            error("Selecciona una localidad de la tabla.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this,
                "Eliminar la localidad " + l.getCategoria() + "?", "Confirmar",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            Resultado r = ctrl.eliminarLocalidad(l.getId());
            mensaje(r);
            if (r.isExito()) cargarLocalidades();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    /* =================================================== TAB REPORTE */

    private JPanel construirTabReporte() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Estilo.FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        cboPartidoRep.setRenderer(renderPartido());

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtro.setOpaque(false);
        filtro.add(etiqueta("Partido:"));
        filtro.add(cboPartidoRep);
        JButton btnGenerar = Estilo.botonPrimario("Generar reporte");
        btnGenerar.addActionListener(e -> generarReporte());
        JButton btnPdf = Estilo.botonPlano("Guardar PDF");
        btnPdf.addActionListener(e -> guardarReportePdf());
        filtro.add(btnGenerar);
        filtro.add(btnPdf);
        p.add(filtro, BorderLayout.NORTH);

        JTable t = new JTable(mReporte);
        Estilo.tabla(t);
        p.add(Estilo.scroll(t), BorderLayout.CENTER);
        return p;
    }

    private void generarReporte() {
        Partido p = (Partido) cboPartidoRep.getSelectedItem();
        if (p == null) return;
        try {
            reporteActual.clear();
            reporteActual.addAll(ctrl.resumenVentas(p.getCodigo()));
            mReporte.setRowCount(0);
            for (ResumenLocalidad r : reporteActual) {
                mReporte.addRow(new Object[]{nn(r.getLocalidad()),
                        r.getVendidos(), Moneda.fmt(r.getTotalRecaudado())});
            }
            if (reporteActual.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Este partido aun no registra ventas.",
                        "Reporte", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            error("No se pudo generar el reporte: " + ex.getMessage());
        }
    }

    private void guardarReportePdf() {
        Partido p = (Partido) cboPartidoRep.getSelectedItem();
        if (p == null || reporteActual.isEmpty()) {
            error("Genera primero el reporte.");
            return;
        }
        try {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Guardar reporte PDF");
            fc.setSelectedFile(new File("reporte_ventas_partido_"
                    + p.getCodigo() + ".pdf"));
            if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
            File destino = fc.getSelectedFile();
            if (!destino.getName().toLowerCase().endsWith(".pdf")) {
                destino = new File(destino.getParentFile(), destino.getName() + ".pdf");
            }
            byte[] pdf = GeneradorComprobantePDF.reporteVentas(
                    p.getEquipoLocal() + " vs " + p.getEquipoVisita(),
                    reporteActual, ctrl.getSesion().getNombre());
            Files.write(destino.toPath(), pdf);
            JOptionPane.showMessageDialog(this,
                    "Reporte guardado en:\n" + destino.getAbsolutePath(),
                    "PDF generado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            error("No se pudo generar el PDF: " + ex.getMessage());
        }
    }

    /* ================================================== TAB FACTURAS */

    private JPanel construirTabFacturas() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Estilo.FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acciones.setOpaque(false);
        JButton btnRefrescar = Estilo.botonPlano("Actualizar");
        btnRefrescar.addActionListener(e -> cargarFacturas());
        JButton btnVer = Estilo.botonPrimario("Ver comprobante");
        btnVer.addActionListener(e -> verFactura());
        acciones.add(btnRefrescar);
        acciones.add(btnVer);
        p.add(acciones, BorderLayout.NORTH);

        Estilo.tabla(tFacturas);
        p.add(Estilo.scroll(tFacturas), BorderLayout.CENTER);
        return p;
    }

    private void cargarFacturas() {
        try {
            facturas.clear();
            facturas.addAll(ctrl.todasFacturas());
            mFacturas.setRowCount(0);
            for (Factura f : facturas) {
                mFacturas.addRow(new Object[]{f.getIdFactura(),
                        nn(f.getUsuarioNombre()), nn(f.getFecha()),
                        nn(f.getTipoPago()), Moneda.fmt(f.getTotal())});
            }
        } catch (Exception ex) {
            error("No se pudieron cargar las facturas: " + ex.getMessage());
        }
    }

    private void verFactura() {
        int fila = tFacturas.getSelectedRow();
        if (fila < 0) {
            error("Selecciona una factura de la tabla.");
            return;
        }
        Factura f = facturas.get(tFacturas.convertRowIndexToModel(fila));
        try {
            Factura completa = ctrl.comprobante(f.getIdFactura());
            if (completa != null) f = completa;
        } catch (Exception ignore) { }
        FacturaDialog.mostrar(SwingUtilities.getWindowAncestor(this), f,
                "Factura #" + f.getIdFactura());
    }

    /* ===================================================== utilidades */

    private JLabel etiqueta(String t) {
        JLabel l = new JLabel(t);
        l.setFont(Estilo.fuente(Font.PLAIN, 12));
        l.setForeground(Estilo.TEXTO);
        return l;
    }

    private void mensaje(Resultado r) {
        JOptionPane.showMessageDialog(this, r.getMensaje(),
                r.isExito() ? "Operacion exitosa" : "Operacion rechazada",
                r.isExito() ? JOptionPane.INFORMATION_MESSAGE
                            : JOptionPane.WARNING_MESSAGE);
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso",
                JOptionPane.WARNING_MESSAGE);
    }

    private static String nn(String s) { return s == null ? "" : s; }
}
