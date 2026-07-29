package ec.edu.monster.vista;

import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/** Carga imágenes del classpath (recursos copiados del proyecto web). */
final class Img {

    private Img() { }

    /** JLabel con la imagen escalada a alto={@code h}px (vacío si no carga). */
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
}
