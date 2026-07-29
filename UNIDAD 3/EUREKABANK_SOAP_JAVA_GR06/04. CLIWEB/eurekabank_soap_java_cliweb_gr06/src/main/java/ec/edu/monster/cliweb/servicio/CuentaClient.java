package ec.edu.monster.cliweb.servicio;

import ec.edu.monster.cliweb.ws.ClienteResumen;
import ec.edu.monster.cliweb.ws.CuentaResumen;
import ec.edu.monster.cliweb.ws.Resultado;
import java.util.List;

/**
 * Wrapper de operaciones de cuentas que encamina cada operacion
 * al microservicio correspondiente (ms-deposito, ms-retiro, ms-transferencia, ms-consulta).
 */
public class CuentaClient {

    public Resultado depositar(String cuenta, String monto, String moneda) {
        return WsFactory.deposito().depositar(cuenta, monto, moneda);
    }

    public Resultado retirar(String cuenta, String monto, String moneda) {
        return WsFactory.retiro().retirar(cuenta, monto, moneda);
    }

    public Resultado consultarSaldo(String cuenta) {
        return WsFactory.consulta().consultarSaldo(cuenta);
    }

    public Resultado transferir(String origen, String destino, String monto, String moneda) {
        return WsFactory.transferencia().transferir(origen, destino, monto, moneda);
    }

    public List<CuentaResumen> listarCuentasPorCliente(String cliente) {
        return WsFactory.consulta().listarCuentasPorCliente(cliente);
    }

    public Resultado registrarCliente(String paterno, String materno, String nombre,
            String dni, String ciudad, String direccion, String telefono, String email) {
        return WsFactory.consulta().registrarCliente(paterno, materno, nombre, dni,
                ciudad, direccion, telefono, email);
    }

    public Resultado registrarCuenta(String cliente, String moneda) {
        return WsFactory.consulta().registrarCuenta(cliente, moneda);
    }

    public Resultado eliminarCuenta(String cuenta) {
        return WsFactory.consulta().eliminarCuenta(cuenta);
    }

    public List<ClienteResumen> listarClientes() {
        return WsFactory.consulta().listarClientes();
    }
}
