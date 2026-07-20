package ec.edu.monster.controlador;

import ec.edu.monster.dto.Peticiones;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.persistencia.AuditoriaBpmDAO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** POST /api/auditoria - Registra una operacion finalizada por Bonita. */
@Path("auditoria")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuditoriaResource {

    private static final Logger LOG = Logger.getLogger(AuditoriaResource.class.getName());

    @POST
    public Resultado registrar(Peticiones.Auditoria auditoria) {
        if (auditoria == null || auditoria.getOperacion() == null
                || auditoria.getOperacion().isBlank() || auditoria.getCuentaOrigen() == null
                || auditoria.getCuentaOrigen().isBlank()) {
            return Resultado.error("Operación y cuenta de origen son obligatorias.");
        }
        try {
            new AuditoriaBpmDAO().registrarSiNoExisteReciente(
                    auditoria.getOperacion(), auditoria.getCuentaOrigen(),
                    auditoria.getCuentaDestino(), auditoria.getMonto(), auditoria.getUsuario(),
                    auditoria.getEstado());
            return Resultado.ok("Auditoría BPM registrada en la base de datos.", 0);
        } catch (SQLException | NumberFormatException e) {
            LOG.log(Level.SEVERE, "No se pudo registrar la auditoría BPM", e);
            return Resultado.error("No se pudo registrar la auditoría BPM: " + e.getMessage());
        }
    }
}
