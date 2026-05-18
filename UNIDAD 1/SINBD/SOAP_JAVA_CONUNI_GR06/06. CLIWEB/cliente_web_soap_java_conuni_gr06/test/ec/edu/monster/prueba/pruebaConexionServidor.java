package ec.edu.monster.prueba;

import ec.edu.monster.modelo.ServicioAutenticacion;
import ec.edu.monster.modelo.ServicioLongitud;
import ec.edu.monster.modelo.ServicioMasa;
import ec.edu.monster.modelo.ServicioTemperatura;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas de INTEGRACION: requieren que el servidor SOAP este desplegado en
 *   http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI
 *
 * Si el servidor no responde, las pruebas se SALTAN (no fallan) gracias a
 * Assume.assumeTrue(servidorDisponible()).
 *
 * Para forzar la ejecucion: arranca primero el proyecto servidor en NetBeans
 * (Deploy a GlassFish/Payara) y luego corre estas pruebas.
 */
public class pruebaConexionServidor {

    private static final String URL_WSDL =
            "http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI?wsdl";
    private static final double MARGEN = 0.0001;

    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();
    private final ServicioLongitud servicioLongitud = new ServicioLongitud();
    private final ServicioMasa servicioMasa = new ServicioMasa();
    private final ServicioTemperatura servicioTemperatura = new ServicioTemperatura();

    @Before
    public void verificarServidor() {
        Assume.assumeTrue(
            "Servidor SOAP no disponible en " + URL_WSDL + " — prueba saltada.",
            servidorDisponible());
    }

    // ========== Autenticacion ==========

    @Test
    public void pruebaLoginValido() throws Exception {
        assertTrue(servicioAutenticacion.iniciarSesion("MONSTER", "MONSTER9"));
    }

    @Test
    public void pruebaLoginInvalido() throws Exception {
        assertFalse(servicioAutenticacion.iniciarSesion("MONSTER", "incorrecta"));
    }

    @Test
    public void pruebaLoginUsuarioInexistente() throws Exception {
        assertFalse(servicioAutenticacion.iniciarSesion("noexiste", "x"));
    }

    // ========== Longitud ==========

    @Test
    public void pruebaConversionLongitud() throws Exception {
        assertEquals(32.8084, servicioLongitud.metrosAPies(10.0), MARGEN);
    }

    @Test
    public void pruebaKilometrosAMillas() throws Exception {
        assertEquals(6.21371, servicioLongitud.kilometrosAMillas(10.0), MARGEN);
    }

    // ========== Masa ==========

    @Test
    public void pruebaConversionMasa() throws Exception {
        assertEquals(2.20462, servicioMasa.kilogramosALibras(1.0), MARGEN);
    }

    @Test
    public void pruebaToneladasAKilogramos() throws Exception {
        assertEquals(2000.0, servicioMasa.toneladasAKilogramos(2.0), MARGEN);
    }

    // ========== Temperatura ==========

    @Test
    public void pruebaCelsiusAFahrenheit() throws Exception {
        assertEquals(32.0, servicioTemperatura.celsiusAFahrenheit(0.0), MARGEN);
        assertEquals(212.0, servicioTemperatura.celsiusAFahrenheit(100.0), MARGEN);
    }

    @Test
    public void pruebaCelsiusAKelvin() throws Exception {
        assertEquals(273.15, servicioTemperatura.celsiusAKelvin(0.0), MARGEN);
    }

    // ========== Helper ==========

    private static boolean servidorDisponible() {
        try {
            java.net.URL url = new java.net.URL(URL_WSDL);
            java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
            con.setConnectTimeout(2000);
            con.setReadTimeout(2000);
            con.setRequestMethod("HEAD");
            int code = con.getResponseCode();
            con.disconnect();
            return code >= 200 && code < 400;
        } catch (Exception ex) {
            return false;
        }
    }
}
