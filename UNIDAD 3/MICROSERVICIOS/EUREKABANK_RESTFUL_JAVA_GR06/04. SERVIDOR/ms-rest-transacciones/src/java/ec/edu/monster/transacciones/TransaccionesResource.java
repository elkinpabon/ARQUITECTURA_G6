package ec.edu.monster.transacciones;

import ec.edu.monster.dto.Peticiones;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.servicio.BancoService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransaccionesResource {
    private final BancoService servicio = new BancoService();

    @POST
    @Path("cuentas/{cuenta}/deposito")
    public Resultado depositar(@PathParam("cuenta") String cuenta, Peticiones.Monto monto) {
        return monto == null
                ? Resultado.error("El monto no es un número válido.")
                : servicio.operar(cuenta, monto.getMonto(), monto.getMoneda(), true);
    }

    @POST
    @Path("cuentas/{cuenta}/retiro")
    public Resultado retirar(@PathParam("cuenta") String cuenta, Peticiones.Monto monto) {
        return monto == null
                ? Resultado.error("El monto no es un número válido.")
                : servicio.operar(cuenta, monto.getMonto(), monto.getMoneda(), false);
    }

    @POST
    @Path("transferencias")
    public Resultado transferir(Peticiones.Transferencia transferencia) {
        return transferencia == null
                ? Resultado.error("El monto no es un número válido.")
                : servicio.transferir(transferencia.getOrigen(), transferencia.getDestino(),
                        transferencia.getMonto(), transferencia.getMoneda());
    }
}
