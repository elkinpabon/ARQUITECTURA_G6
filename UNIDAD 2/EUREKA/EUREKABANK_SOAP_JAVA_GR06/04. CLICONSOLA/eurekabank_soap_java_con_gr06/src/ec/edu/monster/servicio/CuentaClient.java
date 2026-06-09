package ec.edu.monster.servicio;

import ec.edu.monster.ws.ClienteResumen;
import ec.edu.monster.ws.CuentaResumen;
import ec.edu.monster.ws.Resultado;
import java.util.List;

/** Wrapper de WSCuenta (mismas operaciones que el cliente web). */
public class CuentaClient {

    public List<CuentaResumen> listarCuentasPorCliente(String criterio) {
        return WsFactory.cuenta().listarCuentasPorCliente(criterio);
    }

    public List<ClienteResumen> listarClientes() {
        return WsFactory.cuenta().listarClientes();
    }

    public Resultado consultarSaldo(String cuenta) {
        return WsFactory.cuenta().consultarSaldo(cuenta);
    }

    public Resultado depositar(String cuenta, String monto, String moneda) {
        return WsFactory.cuenta().depositar(cuenta, monto, moneda);
    }

    public Resultado retirar(String cuenta, String monto, String moneda) {
        return WsFactory.cuenta().retirar(cuenta, monto, moneda);
    }

    public Resultado transferir(String origen, String destino, String monto, String moneda) {
        return WsFactory.cuenta().transferir(origen, destino, monto, moneda);
    }

    public Resultado registrarCliente(String paterno, String materno, String nombre,
            String dni, String ciudad, String direccion, String telefono, String email) {
        return WsFactory.cuenta().registrarCliente(paterno, materno, nombre, dni,
                ciudad, direccion, telefono, email);
    }

    public Resultado registrarCuenta(String cliente, String moneda) {
        return WsFactory.cuenta().registrarCuenta(cliente, moneda);
    }

    public Resultado eliminarCuenta(String cuenta) {
        return WsFactory.cuenta().eliminarCuenta(cuenta);
    }
}
