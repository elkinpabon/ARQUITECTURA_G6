package ec.edu.monster.servicio;

import ec.edu.monster.modelo.ClienteResumen;
import ec.edu.monster.modelo.CuentaModel;
import ec.edu.monster.modelo.CuentaResumen;
import ec.edu.monster.modelo.MovimientoModel;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.persistencia.ClienteDAO;
import ec.edu.monster.persistencia.ConexionBD;
import ec.edu.monster.persistencia.CuentaDAO;
import ec.edu.monster.persistencia.MovimientoDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logica de negocio de cuentas. Deposito y retiro se ejecutan en UNA SOLA
 * transaccion atomica: si algo falla se hace rollback y nada queda a medias.
 */
public class CuentaService {

    private static final Logger LOG = Logger.getLogger(CuentaService.class.getName());

    private static final String EMPLEADO_CAJA = "0001"; // empleado que opera
    private static final String TIPO_DEPOSITO = "003";  // INGRESO
    private static final String TIPO_RETIRO  = "004";   // SALIDA
    private static final String TIPO_TRANSF_IN  = "008"; // Transferencia INGRESO (destino)
    private static final String TIPO_TRANSF_OUT = "009"; // Transferencia SALIDA  (origen)

    private final CuentaDAO cuentaDAO = new CuentaDAO();
    private final MovimientoDAO movimientoDAO = new MovimientoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ec.edu.monster.persistencia.TasaCambioDAO tasaDAO =
            new ec.edu.monster.persistencia.TasaCambioDAO();

    /** Lista todos los clientes registrados (para el combo del admin). */
    public List<ClienteResumen> listarClientes() {
        try {
            return clienteDAO.listarTodos();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando clientes", e);
            return Collections.emptyList();
        }
    }

    /** Deposito: convierte a la moneda de la cuenta y registra el movimiento (003). */
    public Resultado depositar(String codigoCuenta, String monto, String moneda) {
        return operar(codigoCuenta, monto, true, moneda);
    }

    /** Retiro: convierte, valida saldo suficiente y registra el movimiento (004). */
    public Resultado retirar(String codigoCuenta, String monto, String moneda) {
        return operar(codigoCuenta, monto, false, moneda);
    }

    private static double redondear2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    /**
     * Lista las cuentas de un cliente por su codigo o DNI (solo lectura).
     * Devuelve lista vacia si no hay coincidencias o ante error.
     */
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

    /** Registra un cliente nuevo (admin). Devuelve el código asignado. */
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

    /** Crea una cuenta para un cliente existente (admin). */
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

    /** Elimina una cuenta y sus movimientos (admin). Protege la cuenta master. */
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

    /** Consulta de saldo (solo lectura). */
    public Resultado consultarSaldo(String codigoCuenta) {
        try {
            CuentaModel c = cuentaDAO.obtenerPorCodigo(codigoCuenta);
            if (c == null) {
                return Resultado.error("La cuenta no existe.");
            }
            return Resultado.ok("Saldo consultado.", c.getDecCuenSaldo());
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error consultando saldo", e);
            return Resultado.error("Error interno al consultar el saldo.");
        }
    }

    private Resultado operar(String codigoCuenta, String montoTexto,
                             boolean esDeposito, String monedaMonto) {
        // 1. Validacion de entrada
        if (codigoCuenta == null || codigoCuenta.isBlank()) {
            return Resultado.error("Código de cuenta requerido.");
        }
        double montoIngresado;
        try {
            montoIngresado = Double.parseDouble(montoTexto);
        } catch (NumberFormatException e) {
            return Resultado.error("El monto no es un número válido.");
        }
        if (montoIngresado <= 0) {
            return Resultado.error("El monto debe ser mayor que cero.");
        }

        Connection cn = null;
        try {
            cn = ConexionBD.conectar();
            cn.setAutoCommit(false); // inicia transaccion

            // 2. Bloquea la fila de la cuenta
            CuentaModel cuenta = cuentaDAO.obtenerParaActualizar(cn, codigoCuenta);
            if (cuenta == null) {
                cn.rollback();
                return Resultado.error("La cuenta no existe.");
            }
            if (!"ACTIVO".equalsIgnoreCase(cuenta.getVchCuenEstado())) {
                cn.rollback();
                return Resultado.error("La cuenta no está ACTIVA (estado: "
                        + cuenta.getVchCuenEstado() + ").");
            }

            // 2b. Conversión a la moneda de la cuenta
            String monedaCuenta = cuenta.getChrMoneCodigo();
            String monIn = (monedaMonto == null || monedaMonto.isBlank())
                    ? monedaCuenta : monedaMonto;
            double tasa = tasaDAO.tasa(cn, monIn, monedaCuenta);
            double monto = redondear2(montoIngresado * tasa);
            boolean huboConversion = !monIn.equals(monedaCuenta);

            // 3. Reglas de negocio (en la moneda de la cuenta)
            double delta;
            String tipo;
            if (esDeposito) {
                delta = monto;
                tipo = TIPO_DEPOSITO;
            } else {
                if (cuenta.getDecCuenSaldo() < monto) {
                    cn.rollback();
                    return Resultado.error("Saldo insuficiente. Saldo actual: "
                            + cuenta.getDecCuenSaldo());
                }
                delta = -monto;
                tipo = TIPO_RETIRO;
            }

            // 4. Actualiza saldo + contador
            int filas = cuentaDAO.actualizarSaldo(cn, codigoCuenta, delta);
            if (filas == 0) {
                cn.rollback();
                return Resultado.error("No se pudo actualizar el saldo.");
            }

            // 5. Registra el movimiento (numero atomico dentro de la tx)
            MovimientoModel mov = new MovimientoModel();
            mov.setCodigoCuenta(codigoCuenta);
            mov.setNumeroMovimiento(movimientoDAO.siguienteNumero(cn, codigoCuenta));
            mov.setFechaMovimiento(LocalDate.now().toString());
            mov.setCodigoEmpleado(EMPLEADO_CAJA);
            mov.setCodigoTipoMovimiento(tipo);
            mov.setImporteMovimiento(monto);
            mov.setCuentaReferencia(null);
            if (huboConversion) {
                mov.setMonedaOrigen(monIn);
                mov.setImporteOrigen(redondear2(montoIngresado));
                mov.setTasaAplicada(tasa);
            }
            movimientoDAO.insertar(cn, mov);

            // 6. Confirma
            cn.commit();
            double nuevoSaldo = cuenta.getDecCuenSaldo() + delta;
            String extra = huboConversion
                    ? " (" + redondear2(montoIngresado) + " " + monIn + " → "
                      + monto + " " + monedaCuenta + ", tasa " + tasa + ")"
                    : "";
            return Resultado.ok(
                    (esDeposito ? "Depósito" : "Retiro")
                            + " realizado correctamente." + extra,
                    nuevoSaldo);

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error en operacion bancaria, se hace rollback", e);
            rollbackSilencioso(cn);
            return Resultado.error("Error interno en la operación. Se revirtieron los cambios.");
        } finally {
            restaurarYDesconectar(cn);
        }
    }

    /**
     * Transferencia entre dos cuentas en UNA transaccion atomica:
     * debita el origen, acredita el destino y registra DOS movimientos
     * (009 SALIDA en origen, 008 INGRESO en destino) que se reflejan en
     * los movimientos de ambas cuentas.
     */
    public Resultado transferir(String origen, String destino,
                                String montoTexto, String monedaMonto) {
        if (origen == null || origen.isBlank() || destino == null || destino.isBlank()) {
            return Resultado.error("Cuenta de origen y destino requeridas.");
        }
        origen = origen.trim();
        destino = destino.trim();
        if (origen.equals(destino)) {
            return Resultado.error("La cuenta destino debe ser distinta a la de origen.");
        }
        double montoIngresado;
        try {
            montoIngresado = Double.parseDouble(montoTexto);
        } catch (NumberFormatException e) {
            return Resultado.error("El monto no es un número válido.");
        }
        if (montoIngresado <= 0) {
            return Resultado.error("El monto debe ser mayor que cero.");
        }

        Connection cn = null;
        try {
            cn = ConexionBD.conectar();
            cn.setAutoCommit(false);

            // Bloqueo en orden determinístico para evitar deadlocks.
            String first  = origen.compareTo(destino) < 0 ? origen : destino;
            String second = origen.compareTo(destino) < 0 ? destino : origen;
            CuentaModel cFirst  = cuentaDAO.obtenerParaActualizar(cn, first);
            CuentaModel cSecond = cuentaDAO.obtenerParaActualizar(cn, second);
            CuentaModel cOrig = origen.equals(first) ? cFirst : cSecond;
            CuentaModel cDest = destino.equals(first) ? cFirst : cSecond;

            if (cOrig == null) {
                cn.rollback();
                return Resultado.error("La cuenta de origen no existe.");
            }
            if (cDest == null) {
                cn.rollback();
                return Resultado.error("La cuenta de destino no existe.");
            }
            if (!"ACTIVO".equalsIgnoreCase(cOrig.getVchCuenEstado())) {
                cn.rollback();
                return Resultado.error("La cuenta de origen no está ACTIVA.");
            }
            if (!"ACTIVO".equalsIgnoreCase(cDest.getVchCuenEstado())) {
                cn.rollback();
                return Resultado.error("La cuenta de destino no está ACTIVA.");
            }
            // Conversión a la moneda de cada cuenta.
            String monOrig = cOrig.getChrMoneCodigo();
            String monDest = cDest.getChrMoneCodigo();
            String monIn = (monedaMonto == null || monedaMonto.isBlank())
                    ? monOrig : monedaMonto;
            double tasaOrig = tasaDAO.tasa(cn, monIn, monOrig);
            double tasaDest = tasaDAO.tasa(cn, monIn, monDest);
            double montoOrigen  = redondear2(montoIngresado * tasaOrig);
            double montoDestino = redondear2(montoIngresado * tasaDest);
            boolean convOrig = !monIn.equals(monOrig);
            boolean convDest = !monIn.equals(monDest);

            if (cOrig.getDecCuenSaldo() < montoOrigen) {
                cn.rollback();
                return Resultado.error("Saldo insuficiente. Saldo actual: "
                        + cOrig.getDecCuenSaldo());
            }

            // Débito origen + crédito destino (cada uno en su moneda).
            if (cuentaDAO.actualizarSaldo(cn, origen, -montoOrigen) == 0
                    || cuentaDAO.actualizarSaldo(cn, destino, montoDestino) == 0) {
                cn.rollback();
                return Resultado.error("No se pudo actualizar el saldo.");
            }

            String hoy = LocalDate.now().toString();

            MovimientoModel salida = new MovimientoModel();
            salida.setCodigoCuenta(origen);
            salida.setNumeroMovimiento(movimientoDAO.siguienteNumero(cn, origen));
            salida.setFechaMovimiento(hoy);
            salida.setCodigoEmpleado(EMPLEADO_CAJA);
            salida.setCodigoTipoMovimiento(TIPO_TRANSF_OUT);
            salida.setImporteMovimiento(montoOrigen);
            salida.setCuentaReferencia(destino);
            if (convOrig) {
                salida.setMonedaOrigen(monIn);
                salida.setImporteOrigen(redondear2(montoIngresado));
                salida.setTasaAplicada(tasaOrig);
            }
            movimientoDAO.insertar(cn, salida);

            MovimientoModel ingreso = new MovimientoModel();
            ingreso.setCodigoCuenta(destino);
            ingreso.setNumeroMovimiento(movimientoDAO.siguienteNumero(cn, destino));
            ingreso.setFechaMovimiento(hoy);
            ingreso.setCodigoEmpleado(EMPLEADO_CAJA);
            ingreso.setCodigoTipoMovimiento(TIPO_TRANSF_IN);
            ingreso.setImporteMovimiento(montoDestino);
            ingreso.setCuentaReferencia(origen);
            if (convDest) {
                ingreso.setMonedaOrigen(monIn);
                ingreso.setImporteOrigen(redondear2(montoIngresado));
                ingreso.setTasaAplicada(tasaDest);
            }
            movimientoDAO.insertar(cn, ingreso);

            cn.commit();
            String det = (convOrig || convDest)
                    ? " [" + redondear2(montoIngresado) + " " + monIn
                      + " → origen " + montoOrigen + " " + monOrig
                      + ", destino " + montoDestino + " " + monDest + "]"
                    : "";
            return Resultado.ok("Transferencia de "
                    + String.format("%.2f", montoIngresado) + " " + monIn
                    + " de " + origen + " a " + destino
                    + " realizada correctamente." + det,
                    cOrig.getDecCuenSaldo() - montoOrigen);

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error en transferencia, se hace rollback", e);
            rollbackSilencioso(cn);
            return Resultado.error("Error interno en la transferencia. Se revirtieron los cambios.");
        } finally {
            restaurarYDesconectar(cn);
        }
    }

    private void rollbackSilencioso(Connection cn) {
        if (cn != null) {
            try {
                cn.rollback();
            } catch (SQLException ex) {
                LOG.log(Level.WARNING, "Error en rollback", ex);
            }
        }
    }

    private void restaurarYDesconectar(Connection cn) {
        if (cn != null) {
            try {
                cn.setAutoCommit(true);
            } catch (SQLException ex) {
                LOG.log(Level.WARNING, "Error restaurando autocommit", ex);
            }
        }
        ConexionBD.desconectar(cn);
    }
}
