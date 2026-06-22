package ec.edu.monster.persistencia;

import ec.edu.monster.modelo.Asiento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Estado de asientos en RESERVA_ASIENTO. Reglas:
 *  - LIBRE   = no hay fila en la tabla (o la reserva expiro).
 *  - RESERVADO = en carrito; expira a los {@value #MINUTOS_EXPIRA} minutos.
 *  - OCUPADO = pagado (ligado a una factura); no expira.
 */
public class ReservaDAO {

    private static final Logger LOG = Logger.getLogger(ReservaDAO.class.getName());
    public static final int MINUTOS_EXPIRA = 10;

    /** Borra reservas (no pagadas) vencidas. Devuelve cuantas libero. */
    public int limpiarExpiradas(Connection cn) throws SQLException {
        String sql = "DELETE FROM RESERVA_ASIENTO WHERE ESTADO='RESERVADO' " +
                     "AND CREADO < DATE_SUB(NOW(), INTERVAL " + MINUTOS_EXPIRA + " MINUTE)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            return ps.executeUpdate();
        }
    }

    /** Asientos NO libres (reservados/ocupados) de una seccion, ya depurando expiradas. */
    public List<Asiento> listarNoLibres(int idSeccion) {
        List<Asiento> out = new ArrayList<>();
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            limpiarExpiradas(cn);
            ps = cn.prepareStatement(
                "SELECT FILA, ASIENTO, ESTADO FROM RESERVA_ASIENTO WHERE ID_SECCION = ?");
            ps.setInt(1, idSeccion);
            rs = ps.executeQuery();
            while (rs.next()) {
                out.add(new Asiento(idSeccion, rs.getString("FILA"),
                        rs.getString("ASIENTO"), rs.getString("ESTADO")));
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando asientos no libres de seccion " + idSeccion, e);
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
        return out;
    }

    /** Reserva un asiento para el usuario. false si ya esta tomado (reservado/ocupado). */
    public boolean reservar(int idUsuario, int idSeccion, String fila, String asiento) {
        Connection cn = null;
        try {
            cn = ConexionBD.conectar();
            limpiarExpiradas(cn);
            String sql = "INSERT INTO RESERVA_ASIENTO " +
                "(ID_SECCION, FILA, ASIENTO, ESTADO, ID_USUARIO, CREADO) VALUES (?, ?, ?, 'RESERVADO', ?, ?)";
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, idSeccion);
                ps.setString(2, fila);
                ps.setString(3, asiento);
                ps.setInt(4, idUsuario);
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            // violacion de uk_asiento => asiento ya tomado
            LOG.log(Level.FINE, "No se pudo reservar (probablemente ya tomado)", e);
            return false;
        } finally {
            ConexionBD.desconectar(cn);
        }
    }

    /** Libera un asiento RESERVADO del propio usuario (no toca OCUPADO ni ajenos). */
    public boolean liberar(int idUsuario, int idSeccion, String fila, String asiento) {
        String sql = "DELETE FROM RESERVA_ASIENTO WHERE ID_SECCION=? AND FILA=? AND ASIENTO=? " +
                     "AND ID_USUARIO=? AND ESTADO='RESERVADO'";
        Connection cn = null;
        try {
            cn = ConexionBD.conectar();
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, idSeccion);
                ps.setString(2, fila);
                ps.setString(3, asiento);
                ps.setInt(4, idUsuario);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Error liberando asiento", e);
            return false;
        } finally {
            ConexionBD.desconectar(cn);
        }
    }

    /** Libera TODAS las reservas (no pagadas) del usuario (vaciar carrito). */
    public int liberarTodasDeUsuario(int idUsuario) {
        String sql = "DELETE FROM RESERVA_ASIENTO WHERE ID_USUARIO=? AND ESTADO='RESERVADO'";
        Connection cn = null;
        try {
            cn = ConexionBD.conectar();
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                return ps.executeUpdate();
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Error liberando reservas del usuario " + idUsuario, e);
            return 0;
        } finally {
            ConexionBD.desconectar(cn);
        }
    }

    /**
     * Marca como OCUPADO (pagado) todas las reservas RESERVADO del usuario y las
     * liga a la factura. Se ejecuta dentro de la transaccion de la compra.
     */
    public int ocuparDeUsuario(Connection cn, int idUsuario, int idFactura) throws SQLException {
        String sql = "UPDATE RESERVA_ASIENTO SET ESTADO='OCUPADO', ID_FACTURA=? " +
                     "WHERE ID_USUARIO=? AND ESTADO='RESERVADO'";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idFactura);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate();
        }
    }
}
