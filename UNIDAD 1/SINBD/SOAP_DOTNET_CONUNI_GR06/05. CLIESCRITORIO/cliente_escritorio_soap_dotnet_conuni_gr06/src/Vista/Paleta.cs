using System.Drawing;

namespace Ec.Edu.Monster.Vista;

public static class Paleta
{
    public static readonly Color AZUL = Color.FromArgb(0x1F, 0x3A, 0x5F);
    public static readonly Color AZUL_CLARO = Color.FromArgb(0x2D, 0x4F, 0x7A);
    public static readonly Color AMARILLO = Color.FromArgb(0xFF, 0xD9, 0x66);
    public static readonly Color GRIS_FONDO = Color.FromArgb(0xF2, 0xF4, 0xF7);
    public static readonly Color VERDE_EXITO_BG = Color.FromArgb(0xE2, 0xFD, 0xE2);
    public static readonly Color VERDE_EXITO_FG = Color.FromArgb(0x00, 0x6B, 0x00);
    public static readonly Color ROJO_ERROR_BG = Color.FromArgb(0xFD, 0xE2, 0xE2);
    public static readonly Color ROJO_ERROR_FG = Color.FromArgb(0xA1, 0x00, 0x00);
    public static readonly Color TEXTO_SUAVE = Color.FromArgb(0x6B, 0x75, 0x85);

    public static readonly Font TITULO = new Font("SansSerif", 22, FontStyle.Bold);
    public static readonly Font SUBTITULO = new Font("SansSerif", 14, FontStyle.Regular);
    public static readonly Font ETIQUETA = new Font("SansSerif", 13, FontStyle.Bold);
    public static readonly Font CAMPO = new Font("SansSerif", 14, FontStyle.Regular);
}
