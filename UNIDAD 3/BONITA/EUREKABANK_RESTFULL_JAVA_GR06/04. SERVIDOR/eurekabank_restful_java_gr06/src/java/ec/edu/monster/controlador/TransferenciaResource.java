package ec.edu.monster.controlador;

import ec.edu.monster.dto.Peticiones;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.persistencia.AuditoriaBpmDAO;
import ec.edu.monster.servicio.CuentaService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** /api/transferencias — transferencia entre cuentas (con conversión). */
@Path("transferencias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransferenciaResource {

    private static final Logger LOG = Logger.getLogger(TransferenciaResource.class.getName());
    private final CuentaService cuentaService = new CuentaService();

    @POST
    public Resultado transferir(Peticiones.Transferencia t) {
        Resultado resultado = cuentaService.transferir(t.getOrigen(), t.getDestino(),
                t.getMonto(), t.getMoneda());
        if (resultado.isExito()) {
            try {
                new AuditoriaBpmDAO().registrarSiNoExisteReciente(
                        "TRANSFERENCIA", t.getOrigen(), t.getDestino(),
                        t.getMonto(), "GR06", "FINALIZADO");
            } catch (SQLException | NumberFormatException e) {
                LOG.log(Level.SEVERE, "No se pudo registrar la auditoría REST", e);
            }
        }
        return resultado;
    }
}
