package ec.edu.monster.prueba;

import ec.edu.monster.servicio.ServicioAutenticacion;
import org.junit.Test;
import static org.junit.Assert.*;

public class pruebaAutenticacion {

    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();

    @Test
    public void pruebaAutenticacionExitosa() {
        assertTrue(servicioAutenticacion.autenticar("MONSTER", "MONSTER9"));
    }

    @Test
    public void pruebaUsuarioIncorrecto() {
        assertFalse(servicioAutenticacion.autenticar("USUARIO", "MONSTER9"));
    }

    @Test
    public void pruebaContrasenaIncorrecta() {
        assertFalse(servicioAutenticacion.autenticar("MONSTER", "INCORRECTA"));
    }

    @Test
    public void pruebaCredencialesVacias() {
        assertFalse(servicioAutenticacion.autenticar("", ""));
    }

    @Test
    public void pruebaCredencialesNulas() {
        assertFalse(servicioAutenticacion.autenticar(null, null));
    }

    @Test
    public void pruebaSensibleAMayusculas() {
        assertFalse(servicioAutenticacion.autenticar("monster", "monster9"));
    }
}
