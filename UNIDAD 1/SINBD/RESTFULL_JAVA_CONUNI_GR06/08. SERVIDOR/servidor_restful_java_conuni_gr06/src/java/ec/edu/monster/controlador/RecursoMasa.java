package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.servicio.ServicioMasa;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * Recurso JAX-RS para conversiones de masa.
 */
@Path("/masa")
@Produces(MediaType.APPLICATION_JSON)
public class RecursoMasa {

    private final ServicioMasa servicioMasa = new ServicioMasa();

    @GET
    @Path("/kilogramos-a-libras")
    public Resultado kilogramosALibras(@QueryParam("valor") double valor) {
        return Resultado.ok(servicioMasa.kilogramosALibras(valor));
    }

    @GET
    @Path("/gramos-a-onzas")
    public Resultado gramosAOnzas(@QueryParam("valor") double valor) {
        return Resultado.ok(servicioMasa.gramosAOnzas(valor));
    }

    @GET
    @Path("/toneladas-a-kilogramos")
    public Resultado toneladasAKilogramos(@QueryParam("valor") double valor) {
        return Resultado.ok(servicioMasa.toneladasAKilogramos(valor));
    }

    @GET
    @Path("/libras-a-onzas")
    public Resultado librasAOnzas(@QueryParam("valor") double valor) {
        return Resultado.ok(servicioMasa.librasAOnzas(valor));
    }

    @GET
    @Path("/miligramos-a-gramos")
    public Resultado miligramosAGramos(@QueryParam("valor") double valor) {
        return Resultado.ok(servicioMasa.miligramosAGramos(valor));
    }
}
