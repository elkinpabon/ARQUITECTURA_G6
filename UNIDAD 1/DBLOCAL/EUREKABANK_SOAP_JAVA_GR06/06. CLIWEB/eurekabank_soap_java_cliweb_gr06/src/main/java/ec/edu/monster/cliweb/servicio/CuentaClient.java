package ec.edu.monster.cliweb.servicio;

import ec.edu.monster.cliweb.ws.ClienteResumen;
import ec.edu.monster.cliweb.ws.CuentaResumen;
import ec.edu.monster.cliweb.ws.Resultado;
import ec.edu.monster.cliweb.ws.WSCuenta;
import java.util.List;

/** Wrapper del WSCuenta. Endpoint configurable vía WsFactory/ServidorConfig. */
public class CuentaClient {

    private WSCuenta port() {
        return WsFactory.cuenta();
    }

    public Resultado depositar(String cuenta, String monto, String moneda) {
        return port().depositar(cuenta, monto, moneda);
    }

    public Resultado retirar(String cuenta, String monto, String moneda) {
        return port().retirar(cuenta, monto, moneda);
    }

    public Resultado consultarSaldo(String cuenta) {
        return port().consultarSaldo(cuenta);
    }

    public Resultado transferir(String origen, String destino, String monto, String moneda) {
        return port().transferir(origen, destino, monto, moneda);
    }

    public List<CuentaResumen> listarCuentasPorCliente(String cliente) {
        return port().listarCuentasPorCliente(cliente);
    }

    public Resultado registrarCliente(String paterno, String materno, String nombre,
            String dni, String ciudad, String direccion, String telefono, String email) {
        return port().registrarCliente(paterno, materno, nombre, dni,
                ciudad, direccion, telefono, email);
    }

    public Resultado registrarCuenta(String cliente, String moneda) {
        return port().registrarCuenta(cliente, moneda);
    }

    public Resultado eliminarCuenta(String cuenta) {
        return port().eliminarCuenta(cuenta);
    }

    public List<ClienteResumen> listarClientes() {
        return port().listarClientes();
    }
}
