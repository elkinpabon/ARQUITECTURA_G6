package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.servicio.ServicioLongitud;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * Recurso JAX-RS para conversiones de longitud. Cada endpoint corresponde
 * a una operacion del SOAP CONUNI. El parametro de entrada llega via query string
 * (?valor=X) y la respuesta es un JSON {exito, valor, mensaje}.
 */
@Path("/longitud")
@Produces(MediaType.APPLICATION_JSON)
public class RecursoLongitud {

    private final ServicioLongitud servicioLongitud = new ServicioLongitud();

    @GET
    @Path("/metros-a-pies")
    public Resultado metrosAPies(@QueryParam("valor") double valor) {
        return Resultado.ok(servicioLongitud.metrosAPies(valor));
    }

    @GET
    @Path("/kilometros-a-millas")
    public Resultado kilometrosAMillas(@QueryParam("valor") double valor) {
        return Resultado.ok(servicioLongitud.kilometrosAMillas(valor));
    }

    @GET
    @Path("/centimetros-a-pulgadas")
    public Resultado centimetrosAPulgadas(@QueryParam("valor") double valor) {
        return Resultado.ok(servicioLongitud.centimetrosAPulgadas(valor));
    }

    @GET
    @Path("/yardas-a-metros")
    public Resultado yardasAMetros(@QueryParam("valor") double valor) {
        return Resultado.ok(servicioLongitud.yardasAMetros(valor));
    }

    @GET
    @Path("/milimetros-a-pulgadas")
    public Resultado milimetrosAPulgadas(@QueryParam("valor") double valor) {
        return Resultado.ok(servicioLongitud.milimetrosAPulgadas(valor));
    }
}
