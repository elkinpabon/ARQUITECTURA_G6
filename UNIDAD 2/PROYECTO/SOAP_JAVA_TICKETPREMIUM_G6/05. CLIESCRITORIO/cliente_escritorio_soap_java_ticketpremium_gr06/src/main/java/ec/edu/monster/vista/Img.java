package ec.edu.monster.vista;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Carga imagenes desde el classpath /images/. */
final class Img {

    private Img() { }

    /** JLabel con la imagen escalada a alto={@code h}px (vacio si no carga). */
    static JLabel label(String recurso, int h) {
        try {
            java.net.URL u = Img.class.getResource(recurso);
            if (u != null) {
                Image img = new ImageIcon(u).getImage()
                        .getScaledInstance(-1, h, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(img));
            }
        } catch (Exception ignore) { }
        return new JLabel();
    }

    /**
     * Panel que pinta la imagen escalada al tamano actual del componente,
     * preservando aspect ratio (letterbox). Util para banners responsive.
     */
    static JComponent bannerEscalable(String recurso, Color bg) {
        Image tmp = null;
        try {
            java.net.URL u = Img.class.getResource(recurso);
            if (u != null) tmp = new ImageIcon(u).getImage();
        } catch (Exception ignore) { }
        final Image img = tmp;

        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (img == null) return;
                int w = getWidth(), h = getHeight();
                int iw = img.getWidth(this), ih = img.getHeight(this);
                if (iw <= 0 || ih <= 0) return;
                double s = Math.min((double) w / iw, (double) h / ih);
                int dw = (int) (iw * s), dh = (int) (ih * s);
                int x = (w - dw) / 2, y = (h - dh) / 2;
                g.drawImage(img, x, y, dw, dh, this);
            }
        };
        p.setBackground(bg);
        p.setOpaque(true);
        return p;
    }
}
