package ec.edu.monster.vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Paleta y helpers de estilo del cliente escritorio (diseno formal plano).
 */
final class Estilo {

    private Estilo() { }

    /* ----- Paleta corporativa ----- */
    static final Color AZUL        = new Color(0x0F4C81);  // azul corporativo
    static final Color AZUL_OSCURO = new Color(0x0B3A63);
    static final Color FONDO       = new Color(0xF4F6F9);  // fondo claro
    static final Color SUPERFICIE  = Color.WHITE;
    static final Color BORDE       = new Color(0xDFE3EA);
    static final Color TEXTO       = new Color(0x1F2937);
    static final Color TEXTO_SUAVE = new Color(0x6B7280);
    static final Color EXITO       = new Color(0x15803D);
    static final Color PELIGRO     = new Color(0xB91C1C);

    /* ----- Estados de asiento ----- */
    static final Color ASIENTO_LIBRE     = new Color(0x2E7D32);
    static final Color ASIENTO_RESERVADO = new Color(0xF9A825);
    static final Color ASIENTO_OCUPADO   = new Color(0xC62828);
    static final Color ASIENTO_MIO       = new Color(0x1565C0);

    static Font fuente(int estilo, int tam) {
        return new Font("Segoe UI", estilo, tam);
    }

    static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.BOLD, 17));
        l.setForeground(TEXTO);
        return l;
    }

    static JLabel subtitulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.PLAIN, 12));
        l.setForeground(TEXTO_SUAVE);
        return l;
    }

    static JButton botonPrimario(String texto) {
        JButton b = boton(texto, AZUL, Color.WHITE);
        return b;
    }

    static JButton botonExito(String texto) {
        return boton(texto, EXITO, Color.WHITE);
    }

    static JButton botonPeligro(String texto) {
        return boton(texto, PELIGRO, Color.WHITE);
    }

    static JButton botonPlano(String texto) {
        JButton b = boton(texto, SUPERFICIE, TEXTO);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        return b;
    }

    private static JButton boton(String texto, Color fondo, Color fg) {
        JButton b = new JButton(texto);
        b.setFont(fuente(Font.BOLD, 12));
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(7, 15, 7, 15));
        b.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        // El LAF de Windows repinta el boton gris claro en hover/rollover y deja
        // texto blanco ilegible: pintamos el fondo nosotros y lo oscurecemos al
        // pasar el mouse / presionar, manteniendo siempre el contraste.
        b.setContentAreaFilled(false);
        b.setOpaque(true);
        Color hover = oscurecer(fondo, 0.88f);
        Color pressed = oscurecer(fondo, 0.76f);
        b.setBackground(fondo);
        b.setRolloverEnabled(true);
        b.getModel().addChangeListener(e -> {
            javax.swing.ButtonModel m = b.getModel();
            b.setBackground(m.isPressed() ? pressed : (m.isRollover() ? hover : fondo));
        });
        return b;
    }

    /** Oscurece un color multiplicando sus canales (factor < 1). */
    private static Color oscurecer(Color c, float factor) {
        return new Color(Math.max(0, Math.round(c.getRed() * factor)),
                         Math.max(0, Math.round(c.getGreen() * factor)),
                         Math.max(0, Math.round(c.getBlue() * factor)));
    }

    /** Panel blanco con borde fino (tarjeta plana). */
    static JPanel tarjeta() {
        JPanel p = new JPanel();
        p.setBackground(SUPERFICIE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        return p;
    }

    /** Aplica el estilo plano corporativo a una JTable. */
    static void tabla(JTable t) {
        t.setFont(fuente(Font.PLAIN, 12));
        t.setForeground(TEXTO);
        t.setRowHeight(26);
        t.setGridColor(BORDE);
        t.setShowVerticalLines(false);
        t.setSelectionBackground(new Color(0xDCE8F5));
        t.setSelectionForeground(TEXTO);
        t.setFillsViewportHeight(true);
        t.setAutoCreateRowSorter(true);

        JTableHeader h = t.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(10, 30));
        // El LAF de Windows ignora setBackground() del header (lo pinta claro y
        // las letras blancas se pierden): usamos un renderer propio y opaco que
        // SIEMPRE pinta fondo azul + texto blanco.
        DefaultTableCellRenderer hr = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v,
                    boolean sel, boolean foc, int fila, int col) {
                super.getTableCellRendererComponent(tb, v, false, false, fila, col);
                setFont(fuente(Font.BOLD, 12));   // el LAF pisa la fuente: re-aplicar
                return this;
            }
        };
        hr.setOpaque(true);
        hr.setBackground(AZUL);
        hr.setForeground(Color.WHITE);
        hr.setHorizontalAlignment(JLabel.LEFT);
        hr.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 1, AZUL_OSCURO),
                BorderFactory.createEmptyBorder(0, 8, 0, 8)));
        h.setDefaultRenderer(hr);
    }

    /** ScrollPane con borde plano para tablas. */
    static JScrollPane scroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createLineBorder(BORDE));
        sp.getViewport().setBackground(SUPERFICIE);
        return sp;
    }
}
