package ec.edu.monster.vista;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Formato unificado "$ 1,234.56" igual que escritorio/web. */
final class Moneda {
    private static final DecimalFormat F;
    static {
        DecimalFormatSymbols s = new DecimalFormatSymbols(Locale.US);
        F = new DecimalFormat("#,##0.00", s);
    }
    private Moneda() { }
    static String fmt(BigDecimal v) { return "$ " + (v == null ? "0.00" : F.format(v)); }
}
