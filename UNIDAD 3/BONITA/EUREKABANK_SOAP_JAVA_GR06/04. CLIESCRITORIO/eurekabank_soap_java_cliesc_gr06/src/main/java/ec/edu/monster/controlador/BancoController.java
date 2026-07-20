package ec.edu.monster.controlador;

import ec.edu.monster.servicio.CuentaClient;
import ec.edu.monster.servicio.LoginClient;
import ec.edu.monster.servicio.MovimientoClient;
import ec.edu.monster.servicio.Sesion;
import ec.edu.monster.ws.ClienteResumen;
import ec.edu.monster.ws.CuentaResumen;
import ec.edu.monster.ws.MovimientoModel;
import ec.edu.monster.ws.Resultado;
import java.util.List;

/**
 * Controlador (MVC) de la consola: orquesta servicios y aplica las MISMAS
 * reglas que el cliente web (rol admin/cliente, guard de cuenta propia,
 * depósito solo admin, conversión de moneda).
 */
public class BancoController {

    private final LoginClient loginClient = new LoginClient();
    private final CuentaClient cuentaClient = new CuentaClient();
    private final MovimientoClient movimientoClient = new MovimientoClient();
    private final Sesion sesion = new Sesion();

    public Sesion getSesion() { return sesion; }

    /* ---------- Sesión ---------- */

    public boolean login(String usuario, String clave) {
        if (!loginClient.iniciarSesion(usuario, clave)) {
            return false;
        }
        sesion.setUsuario(usuario);
        boolean admin = "monster".equalsIgnoreCase(usuario);
        sesion.setAdmin(admin);
        sesion.setClienteAsignado(admin ? "" : loginClient.clienteDeUsuario(usuario));
        sesion.setCuentas(null);
        if (!admin) {
            cargarCuentas(sesion.getClienteAsignado());
        }
        return true;
    }

    public void logout() {
        sesion.setUsuario(null);
        sesion.setAdmin(false);
        sesion.setClienteAsignado("");
        sesion.setCuentas(null);
    }

    /* ---------- Clientes / cuentas ---------- */

    public List<ClienteResumen> listarClientes() {
        return cuentaClient.listarClientes();
    }

    /** Carga las cuentas. Si no es admin, se fuerza su cliente asignado. */
    public void cargarCuentas(String criterio) {
        String c = sesion.isAdmin() ? criterio : sesion.getClienteAsignado();
        sesion.setCuentas(cuentaClient.listarCuentasPorCliente(c));
    }

    public List<CuentaResumen> getCuentas() { return sesion.getCuentas(); }

    public double saldoTotal() {
        double t = 0;
        for (CuentaResumen c : sesion.getCuentas()) t += c.getSaldo();
        return t;
    }

    /* ---------- Operaciones ---------- */

    private Resultado denegado(String msg) {
        Resultado r = new Resultado();
        r.setExito(false);
        r.setMensaje(msg);
        return r;
    }

    public Resultado consultarSaldo(String cuenta) {
        if (!sesion.cuentaPropia(cuenta)) {
            return denegado("No tienes acceso a la cuenta " + cuenta + ".");
        }
        return cuentaClient.consultarSaldo(cuenta);
    }

    public Resultado depositar(String cuenta, String monto, String moneda) {
        if (!sesion.isAdmin()) {
            return denegado("Solo el administrador puede depositar. Usa transferencia.");
        }
        if (!sesion.cuentaPropia(cuenta)) {
            return denegado("No tienes acceso a la cuenta " + cuenta + ".");
        }
        return cuentaClient.depositar(cuenta, monto, moneda);
    }

    public Resultado retirar(String cuenta, String monto, String moneda) {
        if (!sesion.cuentaPropia(cuenta)) {
            return denegado("No tienes acceso a la cuenta " + cuenta + ".");
        }
        return cuentaClient.retirar(cuenta, monto, moneda);
    }

    public Resultado transferir(String origen, String destino, String monto, String moneda) {
        if (!sesion.cuentaPropia(origen)) {
            return denegado("No tienes acceso a la cuenta de origen " + origen + ".");
        }
        return cuentaClient.transferir(origen, destino, monto, moneda);
    }

    public List<MovimientoModel> movimientos(String cuenta) {
        if (!sesion.cuentaPropia(cuenta)) {
            return java.util.Collections.emptyList();
        }
        return movimientoClient.listarMovimientos(cuenta);
    }

    /* ---------- Admin ---------- */

    public Resultado registrarCliente(String paterno, String materno, String nombre,
            String dni, String ciudad, String direccion, String telefono, String email) {
        if (!sesion.isAdmin()) {
            return denegado("Solo el administrador puede registrar clientes.");
        }
        return cuentaClient.registrarCliente(paterno, materno, nombre, dni,
                ciudad, direccion, telefono, email);
    }

    public Resultado registrarCuenta(String cliente, String moneda) {
        if (!sesion.isAdmin()) {
            return denegado("Solo el administrador puede registrar cuentas.");
        }
        return cuentaClient.registrarCuenta(cliente, moneda);
    }

    public Resultado eliminarCuenta(String cuenta) {
        if (!sesion.isAdmin()) {
            return denegado("Solo el administrador puede eliminar cuentas.");
        }
        return cuentaClient.eliminarCuenta(cuenta);
    }
}
