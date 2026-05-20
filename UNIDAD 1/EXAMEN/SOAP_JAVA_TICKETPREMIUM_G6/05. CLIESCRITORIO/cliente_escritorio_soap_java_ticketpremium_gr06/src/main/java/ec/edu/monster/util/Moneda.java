package ec.edu.monster.util;

import java.math.BigDecimal;

/** Helpers de formato monetario. */
public final class Moneda {

    private Moneda() { }

    /** Formatea un BigDecimal como "$ 1,234.56". */
    public static String fmt(BigDecimal v) {
        if (v == null) return "$ 0.00";
        return String.format("$ %,.2f", v);
    }
}
