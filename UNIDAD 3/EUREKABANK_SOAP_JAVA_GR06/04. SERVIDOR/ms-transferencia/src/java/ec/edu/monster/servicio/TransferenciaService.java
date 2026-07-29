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

public class TransferenciaService {

    private static final Logger LOG = Logger.getLogger(TransferenciaService.class.getName());

    private static final String EMPLEADO_CAJA = "0001";
    private static final String TIPO_TRANSF_IN  = "008";
    private static final String TIPO_TRANSF_OUT = "009";

    private final CuentaDAO cuentaDAO = new CuentaDAO();
    private final MovimientoDAO movimientoDAO = new MovimientoDAO();
    private final TasaCambioDAO tasaDAO = new TasaCambioDAO();

    public Resultado transferir(String origen, String destino, String montoTexto, String monedaMonto) {
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

            String monOrig = cOrig.getChrMoneCodigo();
            String monDest = cDest.getChrMoneCodigo();
            String monIn = (monedaMonto == null || monedaMonto.isBlank()) ? monOrig : monedaMonto;
            double tasaOrig = tasaDAO.tasa(cn, monIn, monOrig);
            double tasaDest = tasaDAO.tasa(cn, monIn, monDest);
            double montoOrigen  = Math.round(montoIngresado * tasaOrig * 100.0) / 100.0;
            double montoDestino = Math.round(montoIngresado * tasaDest * 100.0) / 100.0;
            boolean convOrig = !monIn.equals(monOrig);
            boolean convDest = !monIn.equals(monDest);

            if (cOrig.getDecCuenSaldo() < montoOrigen) {
                cn.rollback();
                return Resultado.error("Saldo insuficiente. Saldo actual: " + cOrig.getDecCuenSaldo());
            }

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
                salida.setImporteOrigen(Math.round(montoIngresado * 100.0) / 100.0);
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
                ingreso.setImporteOrigen(Math.round(montoIngresado * 100.0) / 100.0);
                ingreso.setTasaAplicada(tasaDest);
            }
            movimientoDAO.insertar(cn, ingreso);

            cn.commit();
            String det = (convOrig || convDest)
                    ? " [" + (Math.round(montoIngresado * 100.0) / 100.0) + " " + monIn
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
            if (cn != null) { try { cn.rollback(); } catch (SQLException ignore) {} }
            return Resultado.error("Error interno en la transferencia. Se revirtieron los cambios.");
        } finally {
            if (cn != null) { try { cn.setAutoCommit(true); } catch (SQLException ignore) {} }
            ConexionBD.desconectar(cn);
        }
    }
}
