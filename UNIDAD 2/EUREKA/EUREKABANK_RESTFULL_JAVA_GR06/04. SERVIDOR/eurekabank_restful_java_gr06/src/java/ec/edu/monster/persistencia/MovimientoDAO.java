package ec.edu.monster.persistencia;

import ec.edu.monster.modelo.MovimientoModel;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de la tabla `movimiento`. */
public class MovimientoDAO {

    /**
     * Lista los movimientos de una cuenta (solo lectura).
     * Abre y cierra su propia conexion.
     */
    public List<MovimientoModel> listarPorCuenta(String codigoCuenta) throws SQLException {
        String sql = "SELECT m.chr_cuencodigo, m.int_movinumero, m.dtt_movifecha, "
                   + "m.chr_emplcodigo, m.chr_tipocodigo, m.dec_moviimporte, "
                   + "m.chr_cuenreferencia, m.chr_movimonori, m.dec_moviimporteori, "
                   + "m.dec_movitasa, t.vch_tipodescripcion AS tipoDescripcion "
                   + "FROM movimiento m "
                   + "INNER JOIN tipomovimiento t ON t.chr_tipocodigo = m.chr_tipocodigo "
                   + "WHERE m.chr_cuencodigo = ? "
                   + "ORDER BY m.dtt_movifecha DESC, m.int_movinumero DESC";
        List<MovimientoModel> lista = new ArrayList<>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setString(1, codigoCuenta);
            rs = ps.executeQuery();
            while (rs.next()) {
                MovimientoModel m = new MovimientoModel();
                m.setCodigoCuenta(rs.getString("chr_cuencodigo"));
                m.setNumeroMovimiento(rs.getInt("int_movinumero"));
                Date f = rs.getDate("dtt_movifecha");
                m.setFechaMovimiento(f != null ? f.toString() : null);
                m.setCodigoEmpleado(rs.getString("chr_emplcodigo"));
                m.setCodigoTipoMovimiento(rs.getString("chr_tipocodigo"));
                m.setTipoDescripcion(rs.getString("tipoDescripcion"));
                m.setImporteMovimiento(rs.getDouble("dec_moviimporte"));
                m.setCuentaReferencia(rs.getString("chr_cuenreferencia"));
                m.setMonedaOrigen(rs.getString("chr_movimonori"));
                double io = rs.getDouble("dec_moviimporteori");
                m.setImporteOrigen(rs.wasNull() ? null : io);
                double tx = rs.getDouble("dec_movitasa");
                m.setTasaAplicada(rs.wasNull() ? null : tx);
                lista.add(m);
            }
            return lista;
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
    }

    /**
     * Calcula el siguiente numero de movimiento para una cuenta
     * (MAX(int_movinumero)+1). Debe ejecutarse DENTRO de la transaccion
     * para que el bloqueo de la fila de cuenta evite colisiones de PK.
     */
    public int siguienteNumero(Connection cn, String codigoCuenta) throws SQLException {
        String sql = "SELECT COALESCE(MAX(int_movinumero), 0) + 1 AS sig "
                   + "FROM movimiento WHERE chr_cuencodigo = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigoCuenta);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("sig") : 1;
            }
        }
    }

    /**
     * Inserta un movimiento. Debe ejecutarse DENTRO de la transaccion
     * (recibe la conexion para mantener atomicidad con el cambio de saldo).
     */
    public void insertar(Connection cn, MovimientoModel m) throws SQLException {
        String sql = "INSERT INTO movimiento (chr_cuencodigo, int_movinumero, dtt_movifecha, "
                   + "chr_emplcodigo, chr_tipocodigo, dec_moviimporte, chr_cuenreferencia, "
                   + "chr_movimonori, dec_moviimporteori, dec_movitasa) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, m.getCodigoCuenta());
            ps.setInt(2, m.getNumeroMovimiento());
            ps.setDate(3, Date.valueOf(m.getFechaMovimiento()));
            ps.setString(4, m.getCodigoEmpleado());
            ps.setString(5, m.getCodigoTipoMovimiento());
            ps.setDouble(6, m.getImporteMovimiento());
            if (m.getCuentaReferencia() == null || m.getCuentaReferencia().isBlank()) {
                ps.setNull(7, java.sql.Types.CHAR);
            } else {
                ps.setString(7, m.getCuentaReferencia());
            }
            if (m.getMonedaOrigen() == null) {
                ps.setNull(8, java.sql.Types.CHAR);
                ps.setNull(9, java.sql.Types.DECIMAL);
                ps.setNull(10, java.sql.Types.DECIMAL);
            } else {
                ps.setString(8, m.getMonedaOrigen());
                ps.setDouble(9, m.getImporteOrigen());
                ps.setDouble(10, m.getTasaAplicada());
            }
            ps.executeUpdate();
        }
    }
}
