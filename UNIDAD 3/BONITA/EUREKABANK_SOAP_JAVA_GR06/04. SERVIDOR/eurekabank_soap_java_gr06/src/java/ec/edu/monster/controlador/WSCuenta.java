package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ClienteResumen;
import ec.edu.monster.modelo.CuentaResumen;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.persistencia.AuditoriaBpmDAO;
import ec.edu.monster.servicio.CuentaService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Fachada SOAP de operaciones de cuenta. */
@WebService(serviceName = "WSCuenta", targetNamespace = "http://ws.monster.edu.ec/")
public class WSCuenta {

    private static final Logger LOG = Logger.getLogger(WSCuenta.class.getName());
    private final CuentaService cuentaService = new CuentaService();

    @WebMethod(operationName = "depositar")
    public Resultado depositar(@WebParam(name = "cuenta") String cuenta,
                               @WebParam(name = "monto") String monto,
                               @WebParam(name = "moneda") String moneda) {
        Resultado resultado = cuentaService.depositar(cuenta, monto, moneda);
        auditarOperacionExitosa(resultado, "DEPOSITO", cuenta, "N/A", monto);
        return resultado;
    }

    @WebMethod(operationName = "retirar")
    public Resultado retirar(@WebParam(name = "cuenta") String cuenta,
                             @WebParam(name = "monto") String monto,
                             @WebParam(name = "moneda") String moneda) {
        Resultado resultado = cuentaService.retirar(cuenta, monto, moneda);
        auditarOperacionExitosa(resultado, "RETIRO", cuenta, "N/A", monto);
        return resultado;
    }

    @WebMethod(operationName = "consultarSaldo")
    public Resultado consultarSaldo(@WebParam(name = "cuenta") String cuenta) {
        return cuentaService.consultarSaldo(cuenta);
    }

    /** Transferencia entre dos cuentas (atomica, registra movimientos en ambas). */
    @WebMethod(operationName = "transferir")
    public Resultado transferir(@WebParam(name = "origen") String origen,
                                @WebParam(name = "destino") String destino,
                                @WebParam(name = "monto") String monto,
                                @WebParam(name = "moneda") String moneda) {
        Resultado resultado = cuentaService.transferir(origen, destino, monto, moneda);
        auditarOperacionExitosa(resultado, "TRANSFERENCIA", origen, destino, monto);
        return resultado;
    }

    /** Lista las cuentas de un cliente buscando por su codigo o DNI. */
    @WebMethod(operationName = "listarCuentasPorCliente")
    public List<CuentaResumen> listarCuentasPorCliente(
            @WebParam(name = "cliente") String cliente) {
        return cuentaService.listarCuentasPorCliente(cliente);
    }

    /** Lista todos los clientes registrados (para el combo del admin). */
    @WebMethod(operationName = "listarClientes")
    public List<ClienteResumen> listarClientes() {
        return cuentaService.listarClientes();
    }

    /** Registra un cliente nuevo (solo admin en el cliente web). */
    @WebMethod(operationName = "registrarCliente")
    public Resultado registrarCliente(@WebParam(name = "paterno") String paterno,
                                      @WebParam(name = "materno") String materno,
                                      @WebParam(name = "nombre") String nombre,
                                      @WebParam(name = "dni") String dni,
                                      @WebParam(name = "ciudad") String ciudad,
                                      @WebParam(name = "direccion") String direccion,
                                      @WebParam(name = "telefono") String telefono,
                                      @WebParam(name = "email") String email) {
        return cuentaService.registrarCliente(paterno, materno, nombre, dni,
                ciudad, direccion, telefono, email);
    }

    /** Crea una cuenta para un cliente existente (solo admin). */
    @WebMethod(operationName = "registrarCuenta")
    public Resultado registrarCuenta(@WebParam(name = "cliente") String cliente,
                                     @WebParam(name = "moneda") String moneda) {
        return cuentaService.registrarCuenta(cliente, moneda);
    }

    /** Elimina una cuenta y sus movimientos (solo admin). */
    @WebMethod(operationName = "eliminarCuenta")
    public Resultado eliminarCuenta(@WebParam(name = "cuenta") String cuenta) {
        return cuentaService.eliminarCuenta(cuenta);
    }

    /** Registra en la base de datos la trazabilidad de una operacion de Bonita. */
    @WebMethod(operationName = "registrarAuditoriaBPM")
    public Resultado registrarAuditoriaBPM(
            @WebParam(name = "operacion") String operacion,
            @WebParam(name = "cuentaOrigen") String cuentaOrigen,
            @WebParam(name = "cuentaDestino") String cuentaDestino,
            @WebParam(name = "monto") String monto,
            @WebParam(name = "usuario") String usuario,
            @WebParam(name = "estado") String estado) {
        try {
            new AuditoriaBpmDAO().registrarSiNoExisteReciente(
                    operacion, cuentaOrigen, cuentaDestino, monto, usuario, estado);
            return Resultado.ok("Auditoria BPM registrada en la base de datos.", 0);
        } catch (SQLException | NumberFormatException e) {
            LOG.log(Level.SEVERE, "No se pudo registrar la auditoria BPM", e);
            return Resultado.error("No se pudo registrar la auditoria BPM: " + e.getMessage());
        }
    }

    private void auditarOperacionExitosa(Resultado resultado, String operacion,
                                         String cuentaOrigen, String cuentaDestino,
                                         String monto) {
        if (!resultado.isExito()) {
            return;
        }
        try {
            new AuditoriaBpmDAO().registrarSiNoExisteReciente(
                    operacion, cuentaOrigen, cuentaDestino, monto, "GR06", "FINALIZADO");
        } catch (SQLException | NumberFormatException e) {
            LOG.log(Level.SEVERE, "No se pudo registrar la auditoria automatica", e);
        }
    }
}
