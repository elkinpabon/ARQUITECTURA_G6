package ec.edu.monster.prueba;

import ec.edu.monster.modelo.FormatoConversion;
import org.junit.Test;
import static org.junit.Assert.*;

public class pruebaFormatoConversion {

    // ========== fmt(double) ==========

    @Test
    public void pruebaFmtEntero() {
        assertEquals("7", FormatoConversion.fmt(7.0));
    }

    @Test
    public void pruebaFmtCero() {
        assertEquals("0", FormatoConversion.fmt(0.0));
    }

    @Test
    public void pruebaFmtNegativoEntero() {
        assertEquals("-273", FormatoConversion.fmt(-273.0));
    }

    @Test
    public void pruebaFmtUnDecimal() {
        assertEquals("7.5", FormatoConversion.fmt(7.5));
    }

    @Test
    public void pruebaFmtCuatroDecimales() {
        assertEquals("22.9659", FormatoConversion.fmt(22.9659));
    }

    @Test
    public void pruebaFmtRedondeaACuatroDecimales() {
        // 32.808398950131235 -> 32.8084 (redondeo)
        assertEquals("32.8084", FormatoConversion.fmt(32.808398950131235));
    }

    @Test
    public void pruebaFmtQuitaCerosSobrantes() {
        // 273.1500 -> 273.15 (no trailing zeros)
        assertEquals("273.15", FormatoConversion.fmt(273.15));
    }

    @Test
    public void pruebaFmtNegativoConDecimales() {
        assertEquals("-7.5", FormatoConversion.fmt(-7.5));
    }

    // ========== formatear(entrada, origen, salida, destino) ==========

    @Test
    public void pruebaFormatearLongitud() {
        // 7 m a pies = 7 * 3.28084 = 22.96588
        assertEquals("7 m = 22.9659 ft",
                FormatoConversion.formatear(7.0, "m", 22.96588, "ft"));
    }

    @Test
    public void pruebaFormatearTemperaturaCero() {
        // 0 °C = 32 °F (entero, sin decimales)
        assertEquals("0 °C = 32 °F",
                FormatoConversion.formatear(0.0, "°C", 32.0, "°F"));
    }

    @Test
    public void pruebaFormatearTemperaturaKelvin() {
        // 0 °C = 273.15 K (no "273.1500 K")
        assertEquals("0 °C = 273.15 K",
                FormatoConversion.formatear(0.0, "°C", 273.15, "K"));
    }

    @Test
    public void pruebaFormatearMasa() {
        // 1 kg = 2.20462 lb -> redondeado a 4 dec = 2.2046
        assertEquals("1 kg = 2.2046 lb",
                FormatoConversion.formatear(1.0, "kg", 2.20462, "lb"));
    }

    // ========== unidades(operacion) ==========

    @Test
    public void pruebaUnidadesMetrosAPies() {
        assertArrayEquals(new String[]{"m", "ft"},
                FormatoConversion.unidades("metrosAPies"));
    }

    @Test
    public void pruebaUnidadesKilometrosAMillas() {
        assertArrayEquals(new String[]{"km", "mi"},
                FormatoConversion.unidades("kilometrosAMillas"));
    }

    @Test
    public void pruebaUnidadesKilogramosALibras() {
        assertArrayEquals(new String[]{"kg", "lb"},
                FormatoConversion.unidades("kilogramosALibras"));
    }

    @Test
    public void pruebaUnidadesGramosAOnzas() {
        assertArrayEquals(new String[]{"g", "oz"},
                FormatoConversion.unidades("gramosAOnzas"));
    }

    @Test
    public void pruebaUnidadesCelsiusAFahrenheit() {
        assertArrayEquals(new String[]{"°C", "°F"},
                FormatoConversion.unidades("celsiusAFahrenheit"));
    }

    @Test
    public void pruebaUnidadesKelvinACelsius() {
        assertArrayEquals(new String[]{"K", "°C"},
                FormatoConversion.unidades("kelvinACelsius"));
    }

    @Test
    public void pruebaUnidadesFahrenheitAKelvin() {
        assertArrayEquals(new String[]{"°F", "K"},
                FormatoConversion.unidades("fahrenheitAKelvin"));
    }

    @Test
    public void pruebaUnidadesOperacionDesconocida() {
        assertArrayEquals(new String[]{"", ""},
                FormatoConversion.unidades("operacionInexistente"));
    }

    @Test
    public void pruebaUnidadesParaLas16Conversiones() {
        String[] todas = {
            "metrosAPies", "kilometrosAMillas", "centimetrosAPulgadas",
            "yardasAMetros", "milimetrosAPulgadas",
            "kilogramosALibras", "gramosAOnzas", "toneladasAKilogramos",
            "librasAOnzas", "miligramosAGramos",
            "celsiusAFahrenheit", "fahrenheitACelsius",
            "celsiusAKelvin", "kelvinACelsius", "fahrenheitAKelvin"
        };
        for (String op : todas) {
            String[] u = FormatoConversion.unidades(op);
            assertNotNull("Sin unidades para " + op, u);
            assertEquals("Origen vacio para " + op, false, u[0].isEmpty());
            assertEquals("Destino vacio para " + op, false, u[1].isEmpty());
        }
    }
}
