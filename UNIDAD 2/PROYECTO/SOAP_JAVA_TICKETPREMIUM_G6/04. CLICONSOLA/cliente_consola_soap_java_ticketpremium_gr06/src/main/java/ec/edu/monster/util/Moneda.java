package ec.edu.monster.util;

import java.math.BigDecimal;

/** Formato monetario simple. */
public final class Moneda {

    private Moneda() { }

    public static String fmt(BigDecimal v) {
        if (v == null) return "$ 0.00";
        return String.format("$ %,.2f", v);
    }
}
