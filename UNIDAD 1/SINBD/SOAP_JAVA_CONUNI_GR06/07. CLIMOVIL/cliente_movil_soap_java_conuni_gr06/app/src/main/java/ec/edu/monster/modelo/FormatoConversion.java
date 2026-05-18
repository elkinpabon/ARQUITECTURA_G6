package ec.edu.monster.modelo;

/**
 * Utilidad para formatear los resultados de conversion.
 */
public final class FormatoConversion {

    private FormatoConversion() {}

    public static String formatear(double entrada, String origen,
                                   double salida, String destino) {
        return fmt(entrada) + " " + origen + " = " + fmt(salida) + " " + destino;
    }

    public static String fmt(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v) && Math.abs(v) < 1e15) {
            return String.valueOf((long) v);
        }
        String s = String.format(java.util.Locale.US, "%.4f", v);
        return s.contains(".") ? s.replaceAll("0+$", "").replaceAll("\\.$", "") : s;
    }

    /** Devuelve {origen, destino} para una operacion del WSDL. */
    public static String[] unidades(String operacion) {
        switch (operacion) {
            case "metrosAPies":          return new String[]{"m",  "ft"};
            case "kilometrosAMillas":    return new String[]{"km", "mi"};
            case "centimetrosAPulgadas": return new String[]{"cm", "in"};
            case "yardasAMetros":        return new String[]{"yd", "m" };
            case "milimetrosAPulgadas":  return new String[]{"mm", "in"};
            case "kilogramosALibras":    return new String[]{"kg", "lb"};
            case "gramosAOnzas":         return new String[]{"g",  "oz"};
            case "toneladasAKilogramos": return new String[]{"t",  "kg"};
            case "librasAOnzas":         return new String[]{"lb", "oz"};
            case "miligramosAGramos":    return new String[]{"mg", "g" };
            case "celsiusAFahrenheit":   return new String[]{"°C", "°F"};
            case "fahrenheitACelsius":   return new String[]{"°F", "°C"};
            case "celsiusAKelvin":       return new String[]{"°C", "K"};
            case "kelvinACelsius":       return new String[]{"K",  "°C"};
            case "fahrenheitAKelvin":    return new String[]{"°F", "K"};
            default:                     return new String[]{"",   ""};
        }
    }
}
