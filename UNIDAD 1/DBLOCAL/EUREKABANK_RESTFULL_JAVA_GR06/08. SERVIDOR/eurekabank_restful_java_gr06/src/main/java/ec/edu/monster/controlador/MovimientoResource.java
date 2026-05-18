package ec.edu.monster.controlador;

import ec.edu.monster.modelo.MovimientoModel;
import ec.edu.monster.servicio.MovimientoService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

/** /api/movimientos?cuenta=00200002 — ordenados por fecha desc en el servidor. */
@Path("movimientos")
@Produces(MediaType.APPLICATION_JSON)
public class MovimientoResource {

    private final MovimientoService movimientoService = new MovimientoService();

    @GET
    public List<MovimientoModel> listar(@QueryParam("cuenta") String cuenta) {
        return movimientoService.listarMovimientos(cuenta);
    }
}
