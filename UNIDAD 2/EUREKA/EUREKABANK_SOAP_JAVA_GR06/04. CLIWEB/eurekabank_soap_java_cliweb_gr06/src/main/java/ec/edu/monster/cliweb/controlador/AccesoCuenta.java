package ec.edu.monster.cliweb.controlador;

import ec.edu.monster.cliweb.ws.CuentaResumen;
import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * Regla de acceso a una cuenta:
 *  - 'monster' (admin) puede operar cualquier cuenta que haya buscado.
 *  - Un usuario restringido solo puede operar las cuentas de SU cliente
 *    (las que quedaron en sesión al iniciar sesión).
 */
final class AccesoCuenta {

    private AccesoCuenta() { }

    @SuppressWarnings("unchecked")
    static boolean permitida(HttpSession session, String cuenta) {
        if (session == null || cuenta == null || cuenta.isBlank()) {
            return false;
        }
        List<CuentaResumen> cuentas =
                (List<CuentaResumen>) session.getAttribute("cuentasCliente");
        if (cuentas == null) {
            return false;
        }
        for (CuentaResumen c : cuentas) {
            if (cuenta.equals(c.getCodigoCuenta())) {
                return true;
            }
        }
        return false;
    }
}
