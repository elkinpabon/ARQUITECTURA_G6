package ec.edu.monster.persistencia;

import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.modelo.Movimiento;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Acceso a CUENTA y MOVIMIENTO (parte bancaria dentro de ticketpremiumDB). */
public class BancoDAO {

    private static final Logger LOG = Logger.getLogger(BancoDAO.class.getName());

    /** id de la cuenta del usuario (0 si no tiene). Usa la conexion dada (transaccional). */
    public int idCuentaDe(Connection cn, int idUsuario) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement("SELECT ID_CUENTA FROM CUENTA WHERE ID_USUARIO = ?")) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    /** Inserta un movimiento (transaccional). Devuelve su id. */
    public int registrarMovimiento(Connection cn, int idCuenta, String tipo, BigDecimal monto,
                                   String descripcion, int idFactura) throws SQLException {
        String sql = "INSERT INTO MOVIMIENTO (ID_CUENTA, FECHA, TIPO, MONTO, DESCRIPCION, ID_FACTURA) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idCuenta);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setString(3, tipo);
            ps.setBigDecimal(4, monto);
            ps.setString(5, descripcion);
            if (idFactura > 0) ps.setInt(6, idFactura); else ps.setNull(6, java.sql.Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    /** Suma delta al saldo (deuda) de la cuenta (transaccional). */
    public void ajustarSaldo(Connection cn, int idCuenta, BigDecimal delta) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "UPDATE CUENTA SET SALDO = SALDO + ? WHERE ID_CUENTA = ?")) {
            ps.setBigDecimal(1, delta);
            ps.setInt(2, idCuenta);
            ps.executeUpdate();
        }
    }

    // ------------------------------------------------------------------ consultas (WS)

    public Cuenta cuentaDe(int idUsuario) {
        String sql = "SELECT ID_CUENTA, ID_USUARIO, NUMERO, SALDO FROM CUENTA WHERE ID_USUARIO = ?";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();
            if (rs.next()) return new Cuenta(rs.getInt("ID_CUENTA"), rs.getInt("ID_USUARIO"),
                    rs.getString("NUMERO"), rs.getBigDecimal("SALDO"));
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error consultando cuenta del usuario " + idUsuario, e);
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
        return null;
    }

    public List<Movimiento> movimientosDe(int idUsuario) {
        List<Movimiento> out = new ArrayList<>();
        String sql = "SELECT m.ID_MOVIMIENTO, m.ID_CUENTA, m.FECHA, m.TIPO, m.MONTO, m.DESCRIPCION, m.ID_FACTURA " +
                     "  FROM MOVIMIENTO m JOIN CUENTA c ON c.ID_CUENTA = m.ID_CUENTA " +
                     " WHERE c.ID_USUARIO = ? ORDER BY m.FECHA DESC, m.ID_MOVIMIENTO DESC";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();
            while (rs.next()) {
                out.add(new Movimiento(rs.getInt("ID_MOVIMIENTO"), rs.getInt("ID_CUENTA"),
                        String.valueOf(rs.getTimestamp("FECHA")), rs.getString("TIPO"),
                        rs.getBigDecimal("MONTO"), rs.getString("DESCRIPCION"), rs.getInt("ID_FACTURA")));
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando movimientos del usuario " + idUsuario, e);
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
        return out;
    }
}
