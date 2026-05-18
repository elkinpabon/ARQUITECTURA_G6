package ec.edu.monster.ws;

import ec.edu.monster.ws.WSCuenta;
import ec.edu.monster.ws.WSCuenta_Service;

/**
 * Wrapper del WSCuenta. El servidor devuelve un Resultado{exito,mensaje,saldo};
 * se conservan firmas booleanas para no romper las vistas y se exponen ademas
 * metodos que devuelven el mensaje de negocio.
 */
public class CuentaService {

    private WSCuenta port() {
        return new WSCuenta_Service().getWSCuentaPort();
    }

    public boolean realizarDeposito(String cuenta, String monto) {
        return port().depositar(cuenta, monto).isExito();
    }

    public boolean realizarRetiro(String cuenta, String monto) {
        return port().retirar(cuenta, monto).isExito();
    }

    public Resultado depositar(String cuenta, String monto) {
        return port().depositar(cuenta, monto);
    }

    public Resultado retirar(String cuenta, String monto) {
        return port().retirar(cuenta, monto);
    }

    public Resultado consultarSaldo(String cuenta) {
        return port().consultarSaldo(cuenta);
    }
}
