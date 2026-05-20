package ec.edu.monster.prueba;

import ec.edu.monster.modelo.Resultado;
import org.junit.Test;
import static org.junit.Assert.*;

public class pruebaResultado {

    @Test
    public void pruebaResultadoOkContieneValor() {
        Resultado r = Resultado.ok("7 m = 22.9659 ft");
        assertTrue(r.isExito());
        assertEquals("7 m = 22.9659 ft", r.getValor());
    }

    @Test
    public void pruebaResultadoOkMensajePorDefecto() {
        Resultado r = Resultado.ok("cualquier cosa");
        assertEquals("Operacion exitosa", r.getMensaje());
    }

    @Test
    public void pruebaResultadoErrorContieneMensaje() {
        Resultado r = Resultado.error("Credenciales invalidas");
        assertFalse(r.isExito());
        assertEquals("Credenciales invalidas", r.getMensaje());
    }

    @Test
    public void pruebaResultadoErrorValorEsNulo() {
        Resultado r = Resultado.error("Algo fallo");
        assertNull(r.getValor());
    }

    @Test
    public void pruebaResultadoOkSiempreEsExitoso() {
        assertTrue(Resultado.ok("").isExito());
    }

    @Test
    public void pruebaResultadoErrorSiempreFalla() {
        assertFalse(Resultado.error("").isExito());
    }
}
