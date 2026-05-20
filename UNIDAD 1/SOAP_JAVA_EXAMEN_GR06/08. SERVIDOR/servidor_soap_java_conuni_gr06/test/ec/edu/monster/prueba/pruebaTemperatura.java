package ec.edu.monster.prueba;

import ec.edu.monster.servicio.ServicioTemperatura;
import org.junit.Test;
import static org.junit.Assert.*;

public class pruebaTemperatura {

    private final ServicioTemperatura servicio = new ServicioTemperatura();
    private static final double MARGEN = 0.0001;

    @Test
    public void pruebaCelsiusAFahrenheit() {
        assertEquals(32.0, servicio.celsiusAFahrenheit(0.0), MARGEN);
        assertEquals(212.0, servicio.celsiusAFahrenheit(100.0), MARGEN);
    }

    @Test
    public void pruebaFahrenheitACelsius() {
        assertEquals(0.0, servicio.fahrenheitACelsius(32.0), MARGEN);
        assertEquals(100.0, servicio.fahrenheitACelsius(212.0), MARGEN);
    }

    @Test
    public void pruebaCelsiusAKelvin() {
        assertEquals(273.15, servicio.celsiusAKelvin(0.0), MARGEN);
        assertEquals(373.15, servicio.celsiusAKelvin(100.0), MARGEN);
    }

    @Test
    public void pruebaKelvinACelsius() {
        assertEquals(0.0, servicio.kelvinACelsius(273.15), MARGEN);
        assertEquals(-273.15, servicio.kelvinACelsius(0.0), MARGEN);
    }

    @Test
    public void pruebaFahrenheitAKelvin() {
        assertEquals(273.15, servicio.fahrenheitAKelvin(32.0), MARGEN);
        assertEquals(373.15, servicio.fahrenheitAKelvin(212.0), MARGEN);
    }
}
