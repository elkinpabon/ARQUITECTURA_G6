package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.servicio.ServicioTemperatura;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * Recurso JAX-RS para conversiones de temperatura.
 */
@Path("/temperatura")
@Produces(MediaType.APPLICATION_JSON)
public class RecursoTemperatura {

    private final ServicioTemperatura servicioTemperatura = new ServicioTemperatura();

    @GET
    @Path("/celsius-a-fahrenheit")
    public Resultado celsiusAFahrenheit(@QueryParam("valor") double valor) {
        return Resultado.ok(servicioTemperatura.celsiusAFahrenheit(valor));
    }

    @GET
    @Path("/fahrenheit-a-celsius")
    public Resultado fahrenheitACelsius(@QueryParam("valor") double valor) {
        return Resultado.ok(servicioTemperatura.fahrenheitACelsius(valor));
    }

    @GET
    @Path("/celsius-a-kelvin")
    public Resultado celsiusAKelvin(@QueryParam("valor") double valor) {
        return Resultado.ok(servicioTemperatura.celsiusAKelvin(valor));
    }

    @GET
    @Path("/kelvin-a-celsius")
    public Resultado kelvinACelsius(@QueryParam("valor") double valor) {
        return Resultado.ok(servicioTemperatura.kelvinACelsius(valor));
    }

    @GET
    @Path("/fahrenheit-a-kelvin")
    public Resultado fahrenheitAKelvin(@QueryParam("valor") double valor) {
        return Resultado.ok(servicioTemperatura.fahrenheitAKelvin(valor));
    }
}
