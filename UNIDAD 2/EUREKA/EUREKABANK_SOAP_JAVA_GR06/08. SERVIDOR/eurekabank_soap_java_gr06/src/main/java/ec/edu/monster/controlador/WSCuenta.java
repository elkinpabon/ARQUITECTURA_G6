package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ClienteResumen;
import ec.edu.monster.modelo.CuentaResumen;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.servicio.CuentaService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.util.List;

/** Fachada SOAP de operaciones de cuenta. */
@WebService(serviceName = "WSCuenta", targetNamespace = "http://ws.monster.edu.ec/")
public class WSCuenta {

    private final CuentaService cuentaService = new CuentaService();

    @WebMethod(operationName = "depositar")
    public Resultado depositar(@WebParam(name = "cuenta") String cuenta,
                               @WebParam(name = "monto") String monto,
                               @WebParam(name = "moneda") String moneda) {
        return cuentaService.depositar(cuenta, monto, moneda);
    }

    @WebMethod(operationName = "retirar")
    public Resultado retirar(@WebParam(name = "cuenta") String cuenta,
                             @WebParam(name = "monto") String monto,
                             @WebParam(name = "moneda") String moneda) {
        return cuentaService.retirar(cuenta, monto, moneda);
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
        return cuentaService.transferir(origen, destino, monto, moneda);
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
}
