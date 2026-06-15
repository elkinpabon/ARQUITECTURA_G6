package com.example.eurekabank_soap_java.controlador;

import com.example.eurekabank_soap_java.modelo.ClienteResumen;
import com.example.eurekabank_soap_java.modelo.CuentaResumen;
import com.example.eurekabank_soap_java.modelo.Movimiento;
import com.example.eurekabank_soap_java.modelo.Resultado;
import com.example.eurekabank_soap_java.modelo.Sesion;
import com.example.eurekabank_soap_java.soap.CuentaService;
import com.example.eurekabank_soap_java.soap.LoginService;
import com.example.eurekabank_soap_java.soap.MovimientoService;

import java.util.Collections;
import java.util.List;

/**
 * Controlador (MVC): mismas reglas que el cliente web —
 * rol admin/cliente, guard de cuenta propia, depósito solo admin,
 * conversión de moneda. Se llama dentro de Async (fuera del hilo UI).
 */
public class BancoController {

    private final LoginService login = new LoginService();
    private final CuentaService cuenta = new CuentaService();
    private final MovimientoService movs = new MovimientoService();
    private final Sesion sesion = Sesion.get();

    public Sesion getSesion() { return sesion; }

    public boolean login(String usuario, String clave) throws Exception {
        if (!login.iniciarSesion(usuario, clave)) return false;
        sesion.setUsuario(usuario);
        boolean admin = "monster".equalsIgnoreCase(usuario);
        sesion.setAdmin(admin);
        sesion.setClienteAsignado(admin ? "" : login.clienteDeUsuario(usuario));
        sesion.setCuentas(null);
        if (!admin) cargarCuentas(sesion.getClienteAsignado());
        return true;
    }

    public void logout() { sesion.limpiar(); }

    public List<ClienteResumen> listarClientes() throws Exception {
        return cuenta.listarClientes();
    }

    public void cargarCuentas(String criterio) throws Exception {
        String c = sesion.isAdmin() ? criterio : sesion.getClienteAsignado();
        sesion.setCuentas(cuenta.listarCuentasPorCliente(c));
    }

    public List<CuentaResumen> getCuentas() { return sesion.getCuentas(); }

    public double saldoTotal() {
        double t = 0;
        for (CuentaResumen c : sesion.getCuentas()) t += c.getSaldo();
        return t;
    }

    private Resultado deny(String m) { return new Resultado(false, m); }

    public Resultado consultarSaldo(String c) throws Exception {
        if (!sesion.cuentaPropia(c)) return deny("No tienes acceso a la cuenta " + c + ".");
        return cuenta.consultarSaldo(c);
    }

    public Resultado depositar(String c, String monto, String mon) throws Exception {
        if (!sesion.isAdmin())
            return deny("Solo el administrador puede depositar. Usa transferencia.");
        if (!sesion.cuentaPropia(c)) return deny("No tienes acceso a la cuenta " + c + ".");
        return cuenta.depositar(c, monto, mon);
    }

    public Resultado retirar(String c, String monto, String mon) throws Exception {
        if (!sesion.cuentaPropia(c)) return deny("No tienes acceso a la cuenta " + c + ".");
        return cuenta.retirar(c, monto, mon);
    }

    public Resultado transferir(String o, String d, String monto, String mon) throws Exception {
        if (!sesion.cuentaPropia(o))
            return deny("No tienes acceso a la cuenta de origen " + o + ".");
        return cuenta.transferir(o, d, monto, mon);
    }

    public List<Movimiento> movimientos(String c) throws Exception {
        if (!sesion.cuentaPropia(c)) return Collections.emptyList();
        return movs.listarMovimientos(c);
    }

    public Resultado registrarCliente(String pat, String mat, String nom, String dni,
            String ciu, String dir, String tel, String email) throws Exception {
        if (!sesion.isAdmin()) return deny("Solo el administrador puede registrar clientes.");
        return cuenta.registrarCliente(pat, mat, nom, dni, ciu, dir, tel, email);
    }

    public Resultado registrarCuenta(String cli, String mon) throws Exception {
        if (!sesion.isAdmin()) return deny("Solo el administrador puede registrar cuentas.");
        return cuenta.registrarCuenta(cli, mon);
    }

    public Resultado eliminarCuenta(String c) throws Exception {
        if (!sesion.isAdmin()) return deny("Solo el administrador puede eliminar cuentas.");
        return cuenta.eliminarCuenta(c);
    }
}
