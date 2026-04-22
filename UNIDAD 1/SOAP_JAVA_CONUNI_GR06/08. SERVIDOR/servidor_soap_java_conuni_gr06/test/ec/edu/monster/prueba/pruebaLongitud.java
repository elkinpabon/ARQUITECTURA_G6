package ec.edu.monster.prueba;

import ec.edu.monster.servicio.ServicioLongitud;
import org.junit.Test;
import static org.junit.Assert.*;

public class pruebaLongitud {

    private final ServicioLongitud servicio = new ServicioLongitud();
    private static final double MARGEN = 0.0001;

    @Test
    public void pruebaMetrosAPies() {
        assertEquals(3.28084, servicio.metrosAPies(1.0), MARGEN);
    }

    @Test
    public void pruebaKilometrosAMillas() {
        assertEquals(6.21371, servicio.kilometrosAMillas(10.0), MARGEN);
    }

    @Test
    public void pruebaCentimetrosAPulgadas() {
        assertEquals(1.0, servicio.centimetrosAPulgadas(2.54), MARGEN);
    }

    @Test
    public void pruebaYardasAMetros() {
        assertEquals(1.0, servicio.yardasAMetros(1.09361), MARGEN);
    }

    @Test
    public void pruebaMilimetrosAPulgadas() {
        assertEquals(0.393701, servicio.milimetrosAPulgadas(10.0), MARGEN);
    }

    @Test
    public void pruebaValorCero() {
        assertEquals(0.0, servicio.metrosAPies(0.0), MARGEN);
    }
}
