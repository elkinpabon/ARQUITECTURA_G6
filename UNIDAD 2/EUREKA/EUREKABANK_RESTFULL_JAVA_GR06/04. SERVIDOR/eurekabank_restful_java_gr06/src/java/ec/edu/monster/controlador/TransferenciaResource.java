package ec.edu.monster.controlador;

import ec.edu.monster.dto.Peticiones;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.servicio.CuentaService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/** /api/transferencias — transferencia entre cuentas (con conversión). */
@Path("transferencias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransferenciaResource {

    private final CuentaService cuentaService = new CuentaService();

    @POST
    public Resultado transferir(Peticiones.Transferencia t) {
        return cuentaService.transferir(t.getOrigen(), t.getDestino(),
                t.getMonto(), t.getMoneda());
    }
}
