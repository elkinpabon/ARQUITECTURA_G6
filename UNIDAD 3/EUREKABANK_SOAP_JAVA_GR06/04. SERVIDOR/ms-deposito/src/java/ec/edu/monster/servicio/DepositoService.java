package ec.edu.monster.servicio;

import ec.edu.monster.modelo.CuentaModel;
import ec.edu.monster.modelo.MovimientoModel;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.persistencia.ConexionBD;
import ec.edu.monster.persistencia.CuentaDAO;
import ec.edu.monster.persistencia.MovimientoDAO;
import ec.edu.monster.persistencia.TasaCambioDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DepositoService {

    private static final Logger LOG = Logger.getLogger(DepositoService.class.getName());

    private static final String EMPLEADO_CAJA = "0001";
    private static final String TIPO_DEPOSITO = "003";

    private final CuentaDAO cuentaDAO = new CuentaDAO();
    private final MovimientoDAO movimientoDAO = new MovimientoDAO();
    private final TasaCambioDAO tasaDAO = new TasaCambioDAO();

    public Resultado depositar(String codigoCuenta, String montoTexto, String monedaMonto) {
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
            cn.setAutoCommit(false);

            CuentaModel cuenta = cuentaDAO.obtenerParaActualizar(cn, codigoCuenta.trim());
            if (cuenta == null) {
                cn.rollback();
                return Resultado.error("La cuenta no existe.");
            }
            if (!"ACTIVO".equalsIgnoreCase(cuenta.getVchCuenEstado())) {
                cn.rollback();
                return Resultado.error("La cuenta no está ACTIVA (estado: "
                        + cuenta.getVchCuenEstado() + ").");
            }

            String monedaCuenta = cuenta.getChrMoneCodigo();
            String monIn = (monedaMonto == null || monedaMonto.isBlank()) ? monedaCuenta : monedaMonto;
            double tasa = tasaDAO.tasa(cn, monIn, monedaCuenta);
            double monto = Math.round(montoIngresado * tasa * 100.0) / 100.0;
            boolean huboConversion = !monIn.equals(monedaCuenta);

            int filas = cuentaDAO.actualizarSaldo(cn, codigoCuenta.trim(), monto);
            if (filas == 0) {
                cn.rollback();
                return Resultado.error("No se pudo actualizar el saldo.");
            }

            MovimientoModel mov = new MovimientoModel();
            mov.setCodigoCuenta(codigoCuenta.trim());
            mov.setNumeroMovimiento(movimientoDAO.siguienteNumero(cn, codigoCuenta.trim()));
            mov.setFechaMovimiento(LocalDate.now().toString());
            mov.setCodigoEmpleado(EMPLEADO_CAJA);
            mov.setCodigoTipoMovimiento(TIPO_DEPOSITO);
            mov.setImporteMovimiento(monto);
            mov.setCuentaReferencia(null);
            if (huboConversion) {
                mov.setMonedaOrigen(monIn);
                mov.setImporteOrigen(Math.round(montoIngresado * 100.0) / 100.0);
                mov.setTasaAplicada(tasa);
            }
            movimientoDAO.insertar(cn, mov);

            cn.commit();
            double nuevoSaldo = cuenta.getDecCuenSaldo() + monto;
            String extra = huboConversion
                    ? " (" + (Math.round(montoIngresado * 100.0) / 100.0) + " " + monIn + " → "
                      + monto + " " + monedaCuenta + ", tasa " + tasa + ")"
                    : "";
            return Resultado.ok("Depósito realizado correctamente." + extra, nuevoSaldo);

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error en depósito, se hace rollback", e);
            if (cn != null) { try { cn.rollback(); } catch (SQLException ignore) {} }
            return Resultado.error("Error interno en el depósito. Se revirtieron los cambios.");
        } finally {
            if (cn != null) { try { cn.setAutoCommit(true); } catch (SQLException ignore) {} }
            ConexionBD.desconectar(cn);
        }
    }
}
