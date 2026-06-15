package ec.edu.monster.util;

/** Catálogo de monedas (tabla `moneda`): muestra el nombre, no el código. */
public final class Moneda {

    private Moneda() { }

    public static String nombre(String codigo) {
        if (codigo == null) return "-";
        switch (codigo.trim()) {
            case "01": return "Soles";
            case "02": return "Dólares";
            default:   return codigo;
        }
    }
}
