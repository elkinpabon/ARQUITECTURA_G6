package ec.edu.monster.controlador;

import ec.edu.monster.dto.Peticiones;
import ec.edu.monster.modelo.CuentaResumen;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.persistencia.AuditoriaBpmDAO;
import ec.edu.monster.servicio.CuentaService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** /api/cuentas — cuentas de un cliente, saldo, depósito, retiro, alta/baja. */
@Path("cuentas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CuentaResource {

    private static final Logger LOG = Logger.getLogger(CuentaResource.class.getName());
    private final CuentaService cuentaService = new CuentaService();

    /** GET /api/cuentas?cliente=00001  (código o DNI) */
    @GET
    public List<CuentaResumen> porCliente(@QueryParam("cliente") String cliente) {
        return cuentaService.listarCuentasPorCliente(cliente);
    }

    @GET
    @Path("{cuenta}/saldo")
    public Resultado saldo(@PathParam("cuenta") String cuenta) {
        return cuentaService.consultarSaldo(cuenta);
    }

    @POST
    @Path("{cuenta}/deposito")
    public Resultado depositar(@PathParam("cuenta") String cuenta,
                               Peticiones.Monto m) {
        Resultado resultado = cuentaService.depositar(cuenta, m.getMonto(), m.getMoneda());
        auditarSiExitosa(resultado, "DEPOSITO", cuenta, "N/A", m.getMonto());
        return resultado;
    }

    @POST
    @Path("{cuenta}/retiro")
    public Resultado retirar(@PathParam("cuenta") String cuenta,
                             Peticiones.Monto m) {
        Resultado resultado = cuentaService.retirar(cuenta, m.getMonto(), m.getMoneda());
        auditarSiExitosa(resultado, "RETIRO", cuenta, "N/A", m.getMonto());
        return resultado;
    }

    /** POST /api/cuentas  body {cliente,moneda} — crea cuenta (admin). */
    @POST
    public Resultado crear(Peticiones.NuevaCuenta c) {
        return cuentaService.registrarCuenta(c.getCliente(), c.getMoneda());
    }

    @DELETE
    @Path("{cuenta}")
    public Resultado eliminar(@PathParam("cuenta") String cuenta) {
        return cuentaService.eliminarCuenta(cuenta);
    }

    private void auditarSiExitosa(Resultado resultado, String operacion, String origen,
                                  String destino, String monto) {
        if (!resultado.isExito()) return;
        try {
            new AuditoriaBpmDAO().registrarSiNoExisteReciente(
                    operacion, origen, destino, monto, "GR06", "FINALIZADO");
        } catch (SQLException | NumberFormatException e) {
            LOG.log(Level.SEVERE, "No se pudo registrar la auditoría REST", e);
        }
    }
}
