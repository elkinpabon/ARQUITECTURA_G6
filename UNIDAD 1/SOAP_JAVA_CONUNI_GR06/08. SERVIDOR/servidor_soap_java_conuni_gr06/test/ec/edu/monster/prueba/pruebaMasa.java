package ec.edu.monster.prueba;

import ec.edu.monster.servicio.ServicioMasa;
import org.junit.Test;
import static org.junit.Assert.*;

public class pruebaMasa {

    private final ServicioMasa servicio = new ServicioMasa();
    private static final double MARGEN = 0.0001;

    @Test
    public void pruebaKilogramosALibras() {
        assertEquals(2.20462, servicio.kilogramosALibras(1.0), MARGEN);
    }

    @Test
    public void pruebaGramosAOnzas() {
        assertEquals(3.5274, servicio.gramosAOnzas(100.0), MARGEN);
    }

    @Test
    public void pruebaToneladasAKilogramos() {
        assertEquals(2000.0, servicio.toneladasAKilogramos(2.0), MARGEN);
    }

    @Test
    public void pruebaLibrasAOnzas() {
        assertEquals(32.0, servicio.librasAOnzas(2.0), MARGEN);
    }

    @Test
    public void pruebaMiligramosAGramos() {
        assertEquals(1.0, servicio.miligramosAGramos(1000.0), MARGEN);
    }

    @Test
    public void pruebaValorCero() {
        assertEquals(0.0, servicio.kilogramosALibras(0.0), MARGEN);
    }
}
