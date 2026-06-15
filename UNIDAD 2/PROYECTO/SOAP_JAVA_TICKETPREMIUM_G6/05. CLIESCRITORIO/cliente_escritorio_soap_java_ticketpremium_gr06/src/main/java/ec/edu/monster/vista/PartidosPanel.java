package ec.edu.monster.vista;

import ec.edu.monster.controlador.TicketController;
import ec.edu.monster.ws.Partido;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Pestana "Partidos": los 72 partidos del Mundial 2026 con boton
 * "Comprar boletos" que abre el MAPA DE ASIENTOS.
 */
public class PartidosPanel extends JPanel {

    private final TicketController ctrl;
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Codigo", "Grupo", "Local", "Visita", "Fecha",
                         "Estadio", "Ciudad"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final List<Partido> partidos = new ArrayList<>();

    public PartidosPanel(TicketController ctrl) {
        this.ctrl = ctrl;
        setLayout(new BorderLayout(0, 10));
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new javax.swing.BoxLayout(titulos, javax.swing.BoxLayout.Y_AXIS));
        titulos.add(Estilo.titulo("Partidos del Mundial FIFA 2026"));
        titulos.add(Estilo.subtitulo("Selecciona un partido y pulsa \"Comprar boletos\" para abrir el mapa de asientos."));
        cab.add(titulos, BorderLayout.WEST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        JButton btnRefrescar = Estilo.botonPlano("Actualizar");
        btnRefrescar.addActionListener(e -> cargar());
        JButton btnComprar = Estilo.botonExito("Comprar boletos");
        btnComprar.addActionListener(e -> abrirMapa());
        acciones.add(btnRefrescar);
        acciones.add(btnComprar);
        cab.add(acciones, BorderLayout.EAST);
        add(cab, BorderLayout.NORTH);

        Estilo.tabla(tabla);
        tabla.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) abrirMapa();
            }
        });
        add(Estilo.scroll(tabla), BorderLayout.CENTER);

        cargar();
    }

    public final void cargar() {
        try {
            partidos.clear();
            partidos.addAll(ctrl.partidosDisponibles());
            modelo.setRowCount(0);
            for (Partido p : partidos) {
                modelo.addRow(new Object[]{p.getCodigo(), nn(p.getGrupo()),
                        nn(p.getEquipoLocal()), nn(p.getEquipoVisita()),
                        nn(p.getFecha()), nn(p.getEstadio()), nn(p.getCiudad())});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar los partidos:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirMapa() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona primero un partido de la tabla.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int idx = tabla.convertRowIndexToModel(fila);
        Partido p = partidos.get(idx);
        new MapaAsientosDialog(SwingUtilities.getWindowAncestor(this), ctrl, p)
                .setVisible(true);
    }

    private static String nn(String s) { return s == null ? "" : s; }
}
