package ec.edu.monster.vista;

import java.awt.Color;
import java.awt.Font;

/**
 * Paleta y fuentes de la marca CONUNI. Centraliza los estilos para mantener
 * coherencia visual entre los paneles de la aplicacion de escritorio.
 */
public final class Paleta {

    public static final Color AZUL            = new Color(0x1F, 0x3A, 0x5F);
    public static final Color AZUL_CLARO      = new Color(0x2D, 0x4F, 0x7A);
    public static final Color AMARILLO        = new Color(0xFF, 0xD9, 0x66);
    public static final Color GRIS_FONDO      = new Color(0xF2, 0xF4, 0xF7);
    public static final Color VERDE_EXITO_BG  = new Color(0xE2, 0xFD, 0xE2);
    public static final Color VERDE_EXITO_FG  = new Color(0x00, 0x6B, 0x00);
    public static final Color ROJO_ERROR_BG   = new Color(0xFD, 0xE2, 0xE2);
    public static final Color ROJO_ERROR_FG   = new Color(0xA1, 0x00, 0x00);
    public static final Color TEXTO_SUAVE     = new Color(0x6B, 0x75, 0x85);

    public static final Font TITULO     = new Font("SansSerif", Font.BOLD, 22);
    public static final Font SUBTITULO  = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font ETIQUETA   = new Font("SansSerif", Font.BOLD, 13);
    public static final Font CAMPO      = new Font("SansSerif", Font.PLAIN, 14);

    private Paleta() { }
}
