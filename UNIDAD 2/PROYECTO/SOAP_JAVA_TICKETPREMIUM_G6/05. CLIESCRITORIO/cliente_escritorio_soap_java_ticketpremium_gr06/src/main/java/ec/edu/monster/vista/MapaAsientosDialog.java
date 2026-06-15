package ec.edu.monster.vista;

import ec.edu.monster.controlador.TicketController;
import ec.edu.monster.modelo.LineaCarrito;
import ec.edu.monster.util.Moneda;
import ec.edu.monster.ws.Asiento;
import ec.edu.monster.ws.Localidad;
import ec.edu.monster.ws.Partido;
import ec.edu.monster.ws.Resultado;
import ec.edu.monster.ws.Seccion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;

/**
 * MAPA DE ASIENTOS (mockup del estadio).
 *
 * Grilla de botones numFilas x asientosPorFila coloreada por estado:
 *   LIBRE = verde, RESERVADO = ambar, OCUPADO = rojo, MI RESERVA = azul.
 * Clic en libre -> reservarAsiento + agregar al carrito.
 * Clic en mi reserva -> liberarAsiento + quitar del carrito.
 * Se refresca automaticamente cada 4 segundos (polling a asientosNoLibres).
 */
public class MapaAsientosDialog extends JDialog {

    private static final int REFRESCO_MS = 4000;

    private final TicketController ctrl;
    private final Partido partido;
    private final String partidoDesc;

    private final JComboBox<Localidad> cboCategoria = new JComboBox<>();
    private final JComboBox<Seccion> cboSeccion = new JComboBox<>();
    private final JPanel panelGrilla = new JPanel(new GridBagLayout());
    private final JLabel lblEstado = new JLabel(" ");
    private final Timer timer;

    /** botones indexados por clave "F{fila}|{asiento}". */
    private final Map<String, JButton> botones = new HashMap<>();
    private boolean cargandoCombos = false;
    private boolean refrescando = false;

    public MapaAsientosDialog(Window owner, TicketController ctrl, Partido partido) {
        super(owner, "Mapa de asientos - Estadio " + nn(partido.getEstadio()),
                ModalityType.APPLICATION_MODAL);
        this.ctrl = ctrl;
        this.partido = partido;
        this.partidoDesc = nn(partido.getEquipoLocal()) + " vs "
                + nn(partido.getEquipoVisita());

        getContentPane().setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(Estilo.FONDO);

        add(construirCabecera(), BorderLayout.NORTH);
        add(construirCentro(), BorderLayout.CENTER);
        add(construirPie(), BorderLayout.SOUTH);

        timer = new Timer(REFRESCO_MS, e -> refrescarAsientos());
        timer.setRepeats(true);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e)  { timer.stop(); }
            @Override public void windowClosing(WindowEvent e) { timer.stop(); }
        });

        cargarCategorias();

        setSize(new Dimension(900, 640));
        setMinimumSize(new Dimension(720, 480));
        setLocationRelativeTo(owner);
    }

    /* --------------------------------------------------------------- UI */

    private JPanel construirCabecera() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setBackground(Estilo.AZUL);
        cab.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel t = new JLabel(partidoDesc);
        t.setFont(Estilo.fuente(Font.BOLD, 16));
        t.setForeground(Color.WHITE);
        JLabel s = new JLabel("Estadio " + nn(partido.getEstadio()) + " - "
                + nn(partido.getCiudad()) + " | " + nn(partido.getFecha())
                + " | Grupo " + nn(partido.getGrupo()));
        s.setFont(Estilo.fuente(Font.PLAIN, 12));
        s.setForeground(new Color(0xCFE0F0));

        JPanel txt = new JPanel(new GridBagLayout());
        txt.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0; g.gridy = 0; txt.add(t, g);
        g.gridy = 1; txt.add(s, g);
        cab.add(txt, BorderLayout.WEST);

        // selectores de categoria y seccion
        JPanel sel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        sel.setOpaque(false);
        JLabel l1 = new JLabel("Categoria:");
        l1.setForeground(Color.WHITE);
        l1.setFont(Estilo.fuente(Font.BOLD, 12));
        JLabel l2 = new JLabel("Seccion:");
        l2.setForeground(Color.WHITE);
        l2.setFont(Estilo.fuente(Font.BOLD, 12));

        cboCategoria.setFont(Estilo.fuente(Font.PLAIN, 12));
        cboCategoria.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Localidad loc) {
                    setText(loc.getCategoria() + " - " + Moneda.fmt(loc.getPrecio())
                            + " (disp. " + loc.getDisponibilidad() + ")");
                }
                return this;
            }
        });
        cboSeccion.setFont(Estilo.fuente(Font.PLAIN, 12));
        cboSeccion.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Seccion sec) {
                    setText(sec.getCodigoSeccion() + " (" + sec.getNumFilas()
                            + " filas x " + sec.getAsientosPorFila() + ")");
                }
                return this;
            }
        });

        cboCategoria.addActionListener(e -> { if (!cargandoCombos) cargarSecciones(); });
        cboSeccion.addActionListener(e -> { if (!cargandoCombos) construirGrilla(); });

        sel.add(l1);
        sel.add(cboCategoria);
        sel.add(l2);
        sel.add(cboSeccion);
        cab.add(sel, BorderLayout.EAST);
        return cab;
    }

    private JPanel construirCentro() {
        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setBackground(Estilo.FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(12, 16, 0, 16));

        // "Cancha" del estadio (mockup)
        JLabel cancha = new JLabel("CANCHA / TERRENO DE JUEGO", SwingConstants.CENTER);
        cancha.setOpaque(true);
        cancha.setBackground(new Color(0x1B5E20));
        cancha.setForeground(Color.WHITE);
        cancha.setFont(Estilo.fuente(Font.BOLD, 12));
        cancha.setPreferredSize(new Dimension(10, 34));
        cancha.setBorder(BorderFactory.createLineBorder(new Color(0x14451A), 2));
        centro.add(cancha, BorderLayout.NORTH);

        panelGrilla.setBackground(Estilo.SUPERFICIE);
        panelGrilla.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane sp = new JScrollPane(panelGrilla);
        sp.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));
        sp.getViewport().setBackground(Estilo.SUPERFICIE);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        centro.add(sp, BorderLayout.CENTER);
        return centro;
    }

    private JPanel construirPie() {
        JPanel pie = new JPanel(new BorderLayout());
        pie.setBackground(Estilo.FONDO);
        pie.setBorder(BorderFactory.createEmptyBorder(8, 16, 12, 16));

        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        leyenda.setOpaque(false);
        leyenda.add(itemLeyenda("Libre", Estilo.ASIENTO_LIBRE));
        leyenda.add(itemLeyenda("Reservado", Estilo.ASIENTO_RESERVADO));
        leyenda.add(itemLeyenda("Ocupado", Estilo.ASIENTO_OCUPADO));
        leyenda.add(itemLeyenda("Mi reserva (clic para liberar)", Estilo.ASIENTO_MIO));
        pie.add(leyenda, BorderLayout.WEST);

        lblEstado.setFont(Estilo.fuente(Font.PLAIN, 11));
        lblEstado.setForeground(Estilo.TEXTO_SUAVE);
        pie.add(lblEstado, BorderLayout.EAST);
        return pie;
    }

    private JPanel itemLeyenda(String texto, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        JPanel dot = new JPanel();
        dot.setPreferredSize(new Dimension(14, 14));
        dot.setBackground(color);
        dot.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));
        JLabel l = new JLabel(texto);
        l.setFont(Estilo.fuente(Font.PLAIN, 11));
        l.setForeground(Estilo.TEXTO);
        p.add(dot);
        p.add(l);
        return p;
    }

    /* ------------------------------------------------------------ datos */

    private void cargarCategorias() {
        try {
            cargandoCombos = true;
            cboCategoria.removeAllItems();
            List<Localidad> locs = ctrl.localidadesDe(partido.getCodigo());
            for (Localidad l : locs) cboCategoria.addItem(l);
            cargandoCombos = false;
            if (cboCategoria.getItemCount() > 0) {
                cboCategoria.setSelectedIndex(0);
                cargarSecciones();
            } else {
                lblEstado.setText("Este partido no tiene localidades disponibles.");
            }
        } catch (Exception ex) {
            cargandoCombos = false;
            error("No se pudieron cargar las localidades: " + ex.getMessage());
        }
    }

    private void cargarSecciones() {
        Localidad loc = (Localidad) cboCategoria.getSelectedItem();
        if (loc == null) return;
        try {
            cargandoCombos = true;
            cboSeccion.removeAllItems();
            List<Seccion> secs = ctrl.seccionesDe(loc.getId());
            for (Seccion s : secs) cboSeccion.addItem(s);
            cargandoCombos = false;
            if (cboSeccion.getItemCount() > 0) {
                cboSeccion.setSelectedIndex(0);
            }
            construirGrilla();
        } catch (Exception ex) {
            cargandoCombos = false;
            error("No se pudieron cargar las secciones: " + ex.getMessage());
        }
    }

    /** Reconstruye la grilla de botones para la seccion seleccionada. */
    private void construirGrilla() {
        timer.stop();
        panelGrilla.removeAll();
        botones.clear();

        Seccion sec = (Seccion) cboSeccion.getSelectedItem();
        if (sec == null) {
            panelGrilla.revalidate();
            panelGrilla.repaint();
            return;
        }

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(2, 2, 2, 2);

        for (int f = 1; f <= sec.getNumFilas(); f++) {
            String fila = "F" + f;
            JLabel lblFila = new JLabel(fila);
            lblFila.setFont(Estilo.fuente(Font.BOLD, 11));
            lblFila.setForeground(Estilo.TEXTO_SUAVE);
            lblFila.setPreferredSize(new Dimension(34, 26));
            g.gridx = 0; g.gridy = f;
            panelGrilla.add(lblFila, g);

            for (int a = 1; a <= sec.getAsientosPorFila(); a++) {
                String asiento = String.valueOf(a);
                JButton b = new JButton(asiento);
                b.setFont(Estilo.fuente(Font.PLAIN, 10));
                b.setPreferredSize(new Dimension(34, 26));
                b.setMargin(new Insets(0, 0, 0, 0));
                b.setFocusPainted(false);
                // evitar que el LAF de Windows pinte gris el asiento en hover
                // (dejaria texto blanco sobre fondo claro): pintamos plano siempre
                b.setContentAreaFilled(false);
                b.setRolloverEnabled(false);
                b.setOpaque(true);
                b.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));
                final String ff = fila, aa = asiento;
                b.addActionListener(e -> clickAsiento(sec, ff, aa, b));
                botones.put(clave(fila, asiento), b);
                g.gridx = a;
                panelGrilla.add(b, g);
            }
        }
        pintarTodoLibre();
        panelGrilla.revalidate();
        panelGrilla.repaint();

        refrescarAsientos();
        timer.restart();
    }

    private void pintarTodoLibre() {
        Seccion sec = (Seccion) cboSeccion.getSelectedItem();
        for (Map.Entry<String, JButton> e : botones.entrySet()) {
            String[] p = e.getKey().split("\\|");
            boolean mio = sec != null
                    && ctrl.getCarrito().contiene(sec.getIdSeccion(), p[0], p[1]);
            pintar(e.getValue(), mio ? Estilo.ASIENTO_MIO : Estilo.ASIENTO_LIBRE,
                    mio ? "MIO" : "LIBRE");
        }
    }

    /** Polling: trae asientos NO libres y recolorea (en hilo aparte). */
    private void refrescarAsientos() {
        final Seccion sec = (Seccion) cboSeccion.getSelectedItem();
        if (sec == null || refrescando) return;
        refrescando = true;
        final int idSeccion = sec.getIdSeccion();

        new SwingWorker<List<Asiento>, Void>() {
            @Override protected List<Asiento> doInBackground() {
                return ctrl.asientosNoLibres(idSeccion);
            }
            @Override protected void done() {
                refrescando = false;
                Seccion actual = (Seccion) cboSeccion.getSelectedItem();
                if (actual == null || actual.getIdSeccion() != idSeccion) return;
                try {
                    aplicarEstados(idSeccion, get());
                    lblEstado.setText("Actualizado "
                            + java.time.LocalTime.now().withNano(0)
                            + " (refresco cada 4 s)");
                } catch (Exception ex) {
                    lblEstado.setText("Sin conexion con el servidor...");
                }
            }
        }.execute();
    }

    private void aplicarEstados(int idSeccion, List<Asiento> noLibres) {
        // 1) base: libre o mi reserva (segun carrito local)
        for (Map.Entry<String, JButton> e : botones.entrySet()) {
            String[] p = e.getKey().split("\\|");
            boolean mio = ctrl.getCarrito().contiene(idSeccion, p[0], p[1]);
            pintar(e.getValue(), mio ? Estilo.ASIENTO_MIO : Estilo.ASIENTO_LIBRE,
                    mio ? "MIO" : "LIBRE");
        }
        // 2) sobreescribir con reservados / ocupados del servidor
        if (noLibres == null) return;
        for (Asiento a : noLibres) {
            JButton b = botones.get(clave(a.getFila(), a.getAsiento()));
            if (b == null) continue;
            boolean mio = ctrl.getCarrito().contiene(idSeccion, a.getFila(), a.getAsiento());
            if (mio) {
                pintar(b, Estilo.ASIENTO_MIO, "MIO");     // mi propia reserva
            } else if ("OCUPADO".equalsIgnoreCase(nn(a.getEstado()))) {
                pintar(b, Estilo.ASIENTO_OCUPADO, "OCUPADO");
            } else {
                pintar(b, Estilo.ASIENTO_RESERVADO, "RESERVADO");
            }
        }
    }

    /**
     * Pinta el boton y guarda su estado logico. No se deshabilita el boton
     * (el LAF de Windows lo pintaria gris); el estado se valida en el click.
     */
    private void pintar(JButton b, Color color, String estado) {
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.putClientProperty("estado", estado);
    }

    /* ----------------------------------------------------------- accion */

    private void clickAsiento(Seccion sec, String fila, String asiento, JButton b) {
        Localidad loc = (Localidad) cboCategoria.getSelectedItem();
        if (loc == null) return;

        String estado = String.valueOf(b.getClientProperty("estado"));
        if ("OCUPADO".equals(estado) || "RESERVADO".equals(estado)) {
            lblEstado.setText("El asiento " + fila + "-" + asiento
                    + " no esta disponible (" + estado.toLowerCase() + ").");
            return;
        }

        boolean mio = ctrl.getCarrito().contiene(sec.getIdSeccion(), fila, asiento);
        try {
            if (mio) {
                // liberar mi reserva y quitar del carrito
                Resultado r = ctrl.liberarAsiento(sec.getIdSeccion(), fila, asiento);
                if (r.isExito()) {
                    ctrl.getCarrito().quitarAsiento(sec.getIdSeccion(), fila, asiento);
                    pintar(b, Estilo.ASIENTO_LIBRE, "LIBRE");
                    lblEstado.setText("Asiento " + fila + "-" + asiento + " liberado.");
                } else {
                    error(r.getMensaje());
                }
            } else {
                // reservar y agregar al carrito
                Resultado r = ctrl.reservarAsiento(sec.getIdSeccion(), fila, asiento);
                if (r.isExito()) {
                    ctrl.getCarrito().agregar(new LineaCarrito(
                            partido.getCodigo(), partidoDesc,
                            sec.getIdSeccion(), nn(sec.getCodigoSeccion()),
                            nn(loc.getCategoria()), fila, asiento, loc.getPrecio()));
                    pintar(b, Estilo.ASIENTO_MIO, "MIO");
                    lblEstado.setText("Asiento " + fila + "-" + asiento
                            + " reservado y agregado al carrito.");
                } else {
                    error(r.getMensaje());
                    refrescarAsientos();
                }
            }
        } catch (Exception ex) {
            error("Error de comunicacion: " + ex.getMessage());
        }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private static String clave(String fila, String asiento) {
        return (fila == null ? "" : fila.toUpperCase()) + "|"
                + (asiento == null ? "" : asiento);
    }

    private static String nn(String s) { return s == null ? "" : s; }
}
