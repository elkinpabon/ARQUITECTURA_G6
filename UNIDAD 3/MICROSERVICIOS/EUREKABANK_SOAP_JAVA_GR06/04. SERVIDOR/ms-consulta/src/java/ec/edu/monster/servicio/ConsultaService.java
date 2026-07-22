package ec.edu.monster.servicio;

import ec.edu.monster.modelo.ClienteResumen;
import ec.edu.monster.modelo.CuentaModel;
import ec.edu.monster.modelo.CuentaResumen;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.persistencia.ClienteDAO;
import ec.edu.monster.persistencia.CuentaDAO;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsultaService {

    private static final Logger LOG = Logger.getLogger(ConsultaService.class.getName());

    private final CuentaDAO cuentaDAO = new CuentaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    public Resultado consultarSaldo(String codigoCuenta) {
        if (codigoCuenta == null || codigoCuenta.isBlank()) {
            return Resultado.error("Código de cuenta requerido.");
        }
        try {
            CuentaModel c = cuentaDAO.obtenerPorCodigo(codigoCuenta.trim());
            if (c == null) {
                return Resultado.error("La cuenta no existe.");
            }
            return Resultado.ok("Saldo consultado.", c.getDecCuenSaldo());
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error consultando saldo", e);
            return Resultado.error("Error interno al consultar el saldo.");
        }
    }

    public List<CuentaResumen> listarCuentasPorCliente(String criterio) {
        if (criterio == null || criterio.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return cuentaDAO.listarPorCliente(criterio.trim());
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando cuentas por cliente", e);
            return Collections.emptyList();
        }
    }

    public List<ClienteResumen> listarClientes() {
        try {
            return clienteDAO.listarTodos();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando clientes", e);
            return Collections.emptyList();
        }
    }

    public Resultado registrarCliente(String paterno, String materno, String nombre,
            String dni, String ciudad, String direccion,
            String telefono, String email) {
        if (paterno == null || paterno.isBlank() || nombre == null || nombre.isBlank()
                || dni == null || dni.isBlank()) {
            return Resultado.error("Apellido paterno, nombre y DNI son obligatorios.");
        }
        try {
            String cod = clienteDAO.insertar(paterno.trim(),
                    materno == null ? "" : materno.trim(), nombre.trim(), dni.trim(),
                    ciudad == null ? "" : ciudad.trim(),
                    direccion == null ? "" : direccion.trim(),
                    telefono == null ? "" : telefono.trim(),
                    email == null ? "" : email.trim());
            return Resultado.ok("Cliente registrado con código " + cod + ".", 0);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error registrando cliente", e);
            return Resultado.error("No se pudo registrar el cliente: " + e.getMessage());
        }
    }

    public Resultado registrarCuenta(String clienteCodigo, String moneda) {
        if (clienteCodigo == null || clienteCodigo.isBlank()) {
            return Resultado.error("Código de cliente requerido.");
        }
        String mon = (moneda == null || moneda.isBlank()) ? "02" : moneda.trim();
        if (!"01".equals(mon) && !"02".equals(mon)) {
            return Resultado.error("Moneda inválida (use 01 Soles o 02 Dólares).");
        }
        try {
            if (!clienteDAO.existe(clienteCodigo.trim())) {
                return Resultado.error("El cliente " + clienteCodigo + " no existe.");
            }
            String cod = cuentaDAO.insertar(clienteCodigo.trim(), mon);
            return Resultado.ok("Cuenta " + cod + " creada para el cliente "
                    + clienteCodigo + ".", 0);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error registrando cuenta", e);
            return Resultado.error("No se pudo registrar la cuenta: " + e.getMessage());
        }
    }

    public Resultado eliminarCuenta(String codigoCuenta) {
        if (codigoCuenta == null || codigoCuenta.isBlank()) {
            return Resultado.error("Código de cuenta requerido.");
        }
        String cod = codigoCuenta.trim();
        if ("00900000".equals(cod)) {
            return Resultado.error("La cuenta MASTER del banco no se puede eliminar.");
        }
        try {
            int filas = cuentaDAO.eliminar(cod);
            if (filas == 0) {
                return Resultado.error("La cuenta " + cod + " no existe.");
            }
            return Resultado.ok("Cuenta " + cod
                    + " eliminada (junto con sus movimientos).", 0);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error eliminando cuenta", e);
            return Resultado.error("No se pudo eliminar la cuenta: " + e.getMessage());
        }
    }
}
